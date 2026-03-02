package com.salah.whatsappclone.message;

import com.salah.whatsappclone.chat.Chat;
import com.salah.whatsappclone.chat.ChatRepository;
import com.salah.whatsappclone.file.FileService;
import com.salah.whatsappclone.file.FileUtils;
import com.salah.whatsappclone.notification.Notification;
import com.salah.whatsappclone.notification.NotificationService;
import com.salah.whatsappclone.notification.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessagRepository messagRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper mapper;
    private final FileService fileService;
    private final NotificationService notificationService;

    public void saveMessage(MessageRequest messageRequest){
        Chat chat = chatRepository.findById(messageRequest.getChatId()).orElseThrow(()->new EntityNotFoundException("chat not found"));

        Message message = new Message();
        message.setContent(messageRequest.getContent());
        message.setChat(chat);
        message.setSenderId(messageRequest.getSenderId());
        message.setReceiverId(messageRequest.getReceiverId());
        message.setType(messageRequest.getType());
        message.setState(MessageState.SENT);


        messagRepository.save(message);

        Notification notification = Notification.builder()
                .chatId(chat.getId()   )
                .messageType(messageRequest.getType())
                .content(messageRequest.getContent())
                .senderId(messageRequest.getSenderId())
                .receiverId(messageRequest.getReceiverId())
                .type(NotificationType.MESSAGE)
                .chatName(chat.getChatName(message.getSenderId()))
                .build();

        notificationService.sendNotification(message.getReceiverId(), notification);



    }

    public List<MesssageResponse> findChetMessages(String chatId){
        return messagRepository.findMessagesByChatId(chatId)
                .stream().map(mapper::toMessageResponse).toList();

    }

    @Transactional
    public void setMessagesToseen(String chatId, Authentication authentication){
        Chat chat=chatRepository.findById(chatId).orElseThrow(()->new EntityNotFoundException("chat not found"));
        final String receiverId=getReceiverId(chat,authentication);
        messagRepository.SetMessagesToSeenByChatId(chatId,MessageState.SEEN);


        Notification notification = Notification.builder()
                .chatId(chat.getId()   )


                .senderId(getSenderId(chat,authentication))
                .receiverId(receiverId)
                .type(NotificationType.SEEN)

                .build();

        notificationService.sendNotification(receiverId, notification);
    }






    public void uploadMediaMessage(String chatId, MultipartFile file, Authentication authentication){
        Chat chat=chatRepository.findById(chatId).orElseThrow(()->new EntityNotFoundException("chat not found"));
        final String senderId=getSenderId(chat,authentication);
        final String receiverId=getReceiverId(chat,authentication);
        final String filePath=fileService.SaveFile(file,senderId);

        Message message=new Message();

        message.setChat(chat);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setType(MessageType.IMAGE);
        message.setState(MessageState.SENT);
        message.setMediaFilePath(filePath);
        messagRepository.save(message);



        Notification notification = Notification.builder()
                .chatId(chat.getId()   )
                .messageType(MessageType.IMAGE)

                .senderId(senderId)
                .receiverId(receiverId)
                .type(NotificationType.IMAGE)
                .media(FileUtils.readFileFromLocation(filePath))
                .build();

        notificationService.sendNotification(receiverId, notification);


    }

    private String getReceiverId(Chat chat, Authentication authentication) {

        if(chat.getSender().getId().equals(authentication.getName())){
           return chat.getReceiver().getId();
        }
        return chat.getSender().getId();
    }



    private String getSenderId(Chat chat, Authentication authentication) {
        if(chat.getReceiver().getId().equals(authentication.getName())){
            chat.getSender().getId();
        }
        return chat.getReceiver().getId();
    }


}
