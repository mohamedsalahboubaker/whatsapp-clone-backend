package com.salah.whatsappclone.message;

import com.salah.whatsappclone.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;

public interface MessagRepository extends JpaRepository<Message,Long> {

    @Query(name=MessageConstants.FIND_MESSAGES_BY_CHAT_ID)
    List<Message> findMessagesByChatId(String chatId);


    @Query(name=MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT)
    @Modifying
    void SetMessagesToSeenByChatId(@Param("chatId") String chatId, @Param("newState") MessageState messageState);
}
