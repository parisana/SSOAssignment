package com.demo.spring_security.controller;

import com.demo.spring_security.domain.Message;
import com.demo.spring_security.domain.MessageForm;
import com.demo.spring_security.domain.User;
import com.demo.spring_security.repositories.MessageRepoService;
import com.demo.spring_security.repositories.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller
public class MainController{
	private final MessageRepoService messageRepoService;
	private final UserRepo userRepo;

	MainController(MessageRepoService messageRepoService, UserRepo userRepo) {
		this.messageRepoService = messageRepoService;
		this.userRepo = userRepo;
	}

	@RequestMapping(value = {"/login"})
	public String getLogin(){
		return "login";
	}
	@RequestMapping(value = "messages")
	public String redirectTomessages(){
		log.debug("***Redirecting to inbox");
		return "redirect:/messages/inbox";
	}
	@RequestMapping(value = "messages/inbox")
	public String getMessages(@AuthenticationPrincipal User currentUser, Model model){
		log.debug("***getting inbox messages");
		model.addAttribute("currentUser", currentUser);
		model.addAttribute("messages", messageRepoService.getAllMessages(currentUser.getId()));

		return "messages/inbox";
	}
	@RequestMapping(value = "messages/compose", params="form", method= RequestMethod.GET)
	public String createForm(@ModelAttribute MessageForm messageForm, @AuthenticationPrincipal User currentUser, Model model) {
		log.debug("***Getting create message form page");
		model.addAttribute("currentUser", currentUser);
		return "messages/compose";
	}

	@RequestMapping(value = "messages/compose", method=RequestMethod.POST)
	public ModelAndView create(@Valid MessageForm messageForm, BindingResult result, RedirectAttributes redirect) {
		log.debug("***Submitting created message");
		Optional<User> optionalUser = userRepo.findByEmail(messageForm.getToEmail());
		if (!optionalUser.isPresent()) {
			result.rejectValue("toEmail", "toEmail", "User not found");
			return new ModelAndView("messages/compose");
		}else {
			if (result.hasErrors())
				return new ModelAndView("messages/compose");
			Message message = new Message();
			message.setSummary(messageForm.getSummary());
			message.setText(messageForm.getText());
			message.setTo(optionalUser.get());

			message = messageRepoService.save(message);
			redirect.addFlashAttribute("globalMessage", "Message added successfully");
			return new ModelAndView("redirect:/messages/show/{message.id}", "message.id", message.getId());
		}
	}
	@RequestMapping(value = "messages/show/{id}", method=RequestMethod.GET)
	public String view(@PathVariable Long id, @AuthenticationPrincipal User currentUser, Model model) {
		log.debug("***Showing message with id: "+id);
		model.addAttribute("message", messageRepoService.findMessageById(id));
		model.addAttribute("currentUser", currentUser);
		return "messages/show";
	}

	@RequestMapping(value = "messages/{id}", method=RequestMethod.DELETE)
	public String delete(@PathVariable("id")Long id, Message message, RedirectAttributes redirect) {
		log.debug("***Deleting message with id: " + id);
		messageRepoService.delete(message);
		redirect.addFlashAttribute("globalMessage", "Message removed successfully");
		return "redirect:/messages/inbox";
	}
}
