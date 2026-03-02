package com.salah.whatsappclone.chat;

import com.salah.whatsappclone.user.User;
import com.salah.whatsappclone.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMapper mapper;
    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public List<ChatResponse> getChatsByReceiverId(Authentication auth  ) {


        final String userId=auth.getName();
        return chatRepository.findChatsBySenderId(userId)
                .stream()
                .map( chat->mapper.toChatResponse(chat,userId) ).toList();

    }



    @Transactional
    public String createChat(String senderId, String receiverId) {
        Optional<Chat> existingchat=chatRepository.findchatByReceiverAndSender(senderId,receiverId);
        if(existingchat.isPresent()) {
            return existingchat.get().getId();
        }
        User sender =userRepository.findByPublicId(senderId)
                .orElseThrow(()-> new EntityNotFoundException("User with " + senderId + " not found"));

        User receiver =userRepository.findByPublicId(receiverId)
                .orElseThrow(()-> new EntityNotFoundException("User with " + receiverId + " not found"));

        Chat chat=new Chat();
        chat.setSender(sender);
        chat.setReceiver(receiver);
        Chat savedchat=chatRepository.save(chat);
        return savedchat.getId();

    }

}
