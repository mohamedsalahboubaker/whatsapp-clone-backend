package com.salah.whatsappclone.chat;

import com.salah.whatsappclone.commun.BaseAuditingEntity;
import com.salah.whatsappclone.message.Message;
import com.salah.whatsappclone.message.MessageState;
import com.salah.whatsappclone.message.MessageType;
import com.salah.whatsappclone.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.GenerationType.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chat")

@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID,
        query = "SELECT DISTINCT c FROM Chat c WHERE c.sender.id= :senderID OR c.receiver.id= :senderId ORDER BY  createdDate ASC ")

@NamedQuery(name=ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID,
query="SELECT DISTINCT c FROM Chat c WHERE (c.sender.id= :senderId AND c.receiver.id = :receiverId) OR (c.sender.id= :receiverId AND c.receiver.id = :senderId)")





public class Chat extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @OneToMany(mappedBy = "chat",fetch = FetchType.EAGER)
    @OrderBy("createdDate DESC")
    private List<Message> messages;



    @Transient
    public String getChatName(final String senderId){
        if(receiver.getId().equals(senderId)){
            return sender.getFirstName() + " " + sender.getLastName();
        }
        return receiver.getFirstName() + " " + receiver.getLastName();

    }

    @Transient
    public Long getUnreadMessages(final String senderId){
        return messages.stream()
                .filter(m->m.getReceiverId().equals(senderId))
                .filter(m-> MessageState.SENT==m.getState())
                .count();
    }

    @Transient
    public String getLastMessage(){
        if (messages!=null && !messages.isEmpty()){

            if(messages.get(0).getType()!= MessageType.TEXT){
                return "Attachment";
            }
            else
                return messages.get(0).getContent();

        }
        return null;
    }

    @Transient
    public LocalDateTime getLastMessageDateTime(){
        if (messages!=null && !messages.isEmpty()){
            return messages.get(0).getCreatedDate();
        }
        return null;

    }

}
