package com.demo.spring_security.repositories;

import com.demo.spring_security.domain.Message;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageRepoService{
	private final MessageRepo messageRepo;

	MessageRepoService(MessageRepo messageRepo) {
		this.messageRepo = messageRepo;
	}

	public List<Message> getAllMessages(Long id) {
		return messageRepo.findByToId(id);
	}

	public Message getMessage(Long id){
		return messageRepo.findById(id).orElseGet(Message::new);
	}

	public Message save(Message message) {
		return messageRepo.save(message);
	}

	public Message findMessageById(Long id) {
		Optional<Message> optionalMessage = messageRepo.findById(id);
		if (optionalMessage.isPresent()) {
			System.out.println(optionalMessage.get().toString());
			return optionalMessage.get();
		}
		else try {
			throw new NotFoundException("Message not found!");
		} catch (NotFoundException e) {
			System.out.println("##########"+e.getMessage());
		}
		return null;
	}

	public void delete(Message message) {
		messageRepo.delete(message);
	}
}
