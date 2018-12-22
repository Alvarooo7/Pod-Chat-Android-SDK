package com.fanap.podchat.persistance;

import android.content.Context;

import com.fanap.podchat.cachemodel.CacheForwardInfo;
import com.fanap.podchat.cachemodel.CacheLastMessageVO;
import com.fanap.podchat.cachemodel.CacheMessageVO;
import com.fanap.podchat.cachemodel.CacheParticipant;
import com.fanap.podchat.cachemodel.CacheReplyInfoVO;
import com.fanap.podchat.cachemodel.ThreadVo;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.LastMessageVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.mainmodel.UserInfo;
import com.fanap.podchat.model.ConversationSummery;
import com.fanap.podchat.model.ForwardInfo;
import com.fanap.podchat.model.MessageVO;
import com.fanap.podchat.model.ReplyInfoVO;
import com.fanap.podchat.persistance.dao.MessageDao;
import com.fanap.podchat.util.Callback;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.List;

public class MessageDatabaseHelper extends BaseDatabaseHelper {

    private MessageDao messageDao;

    public MessageDatabaseHelper(Context context) {
        super(context);
        messageDao = appDatabase.getMessageDao();
    }

    //Cache history
    public void saveHistory(List<CacheMessageVO> messageVOS, long threadId) {
        for (CacheMessageVO messageVO : messageVOS) {
            messageVO.setThreadVoId(threadId);

            if (messageVO.getParticipant() != null) {
                messageVO.setParticipantId(messageVO.getParticipant().getId());
                messageDao.insertParticipant(messageVO.getParticipant());
            }

            if (messageVO.getConversation() != null) {
//                messageDao.insertThread(messageVO.getConversation());
            }

            if (messageVO.getForwardInfo() != null) {
                messageVO.setForwardInfoId(messageVO.getForwardInfo().getId());
                messageDao.insertForwardInfo(messageVO.getForwardInfo());
                if (messageVO.getForwardInfo().getParticipant() != null) {
                    messageVO.getForwardInfo().setParticipantId(messageVO.getForwardInfo().getParticipant().getId());
                    messageDao.insertParticipant(messageVO.getForwardInfo().getParticipant());
                }
            }

            if (messageVO.getReplyInfoVO() != null) {
                messageVO.setReplyInfoVOId(messageVO.getReplyInfoVO().getId());
                messageDao.insertReplyInfoVO(messageVO.getReplyInfoVO());
                if (messageVO.getReplyInfoVO().getParticipant() != null) {
                    messageVO.getReplyInfoVO().setParticipantId(messageVO.getReplyInfoVO().getParticipant().getId());
                    messageDao.insertParticipant(messageVO.getReplyInfoVO().getParticipant());
                }
            }
        }
        messageDao.insertHistories(messageVOS);
    }

    public void saveMessage(CacheMessageVO cacheMessageVO, long threadId) {
        cacheMessageVO.setThreadVoId(threadId);

        if (cacheMessageVO.getParticipant() != null) {
            cacheMessageVO.setParticipantId(cacheMessageVO.getParticipant().getId());
            messageDao.insertParticipant(cacheMessageVO.getParticipant());
        }

        if (cacheMessageVO.getConversation() != null) {
//                messageDao.insertThread(messageVO.getConversation());
        }

        if (cacheMessageVO.getForwardInfo() != null) {
            cacheMessageVO.setForwardInfoId(cacheMessageVO.getForwardInfo().getId());
            messageDao.insertForwardInfo(cacheMessageVO.getForwardInfo());
            if (cacheMessageVO.getForwardInfo().getParticipant() != null) {
                cacheMessageVO.getForwardInfo().setParticipantId(cacheMessageVO.getForwardInfo().getParticipant().getId());
                messageDao.insertParticipant(cacheMessageVO.getForwardInfo().getParticipant());
            }
        }

        if (cacheMessageVO.getReplyInfoVO() != null) {
            cacheMessageVO.setReplyInfoVOId(cacheMessageVO.getReplyInfoVO().getId());
            messageDao.insertReplyInfoVO(cacheMessageVO.getReplyInfoVO());
            if (cacheMessageVO.getReplyInfoVO().getParticipant() != null) {
                cacheMessageVO.getReplyInfoVO().setParticipantId(cacheMessageVO.getReplyInfoVO().getParticipant().getId());
                messageDao.insertParticipant(cacheMessageVO.getReplyInfoVO().getParticipant());
            }
        }

        messageDao.insertMessage(cacheMessageVO);
    }

    public void deleteMessage(long id) {
        messageDao.deleteMessage(id);
    }

    public void updateGetHistoryResponse(Callback callback, List<MessageVO> messageVOS, long threadId, List<CacheMessageVO> cMessageVOS) {
        long count = callback.getCount();
        long offset = callback.getOffset();
        long firstMessageId = callback.getFirstMessageId();
        long lastMessageId = callback.getLastMessageId();
        String order = callback.getOrder();

        List<MessageVO> histories;

        if (order.equals("asc")) {

            if (!Util.isNullOrEmpty(callback.getFirstMessageId()) && !Util.isNullOrEmpty(callback.getLastMessageId())) {
                if (Util.isNullOrEmptyMessageVO(messageVOS)) {
                    if (!Util.isNullOrEmptyMessageVO(getHistories(count, offset, threadId, order,lastMessageId,firstMessageId))) {
                        messageDao.deleteMessageBetweenLastAndFirstASC(threadId, firstMessageId, lastMessageId);
                    }

                } else if (messageVOS.size() == 1) {
                    if (getHistories(count, offset, threadId, order,lastMessageId,firstMessageId).size() > 1) {
                        messageDao.deleteMessageBetweenLastAndFirstASC(threadId, firstMessageId, lastMessageId);
                        saveMessage(cMessageVOS.get(0), threadId);
                    }

                } else if (messageVOS.size() > 1) {
                    int size = getHistories(count, offset, threadId, order,lastMessageId,firstMessageId).size();
                    long firstMesssageId = getHistories(count, offset, threadId, order,lastMessageId,firstMessageId).get(0).getId();
                    long lastMesssageId = getHistories(count, offset, threadId, order,lastMessageId,firstMessageId).get(size - 1).getId();
                    messageDao.deleteMessageBetweenLastAndFirstASC(threadId, firstMesssageId, lastMesssageId);
                    saveHistory(cMessageVOS, threadId);
                }

            } else if (!Util.isNullOrEmpty(callback.getFirstMessageId())) {
                if (Util.isNullOrEmptyMessageVO(messageVOS)) {
                    if (!Util.isNullOrEmptyMessageVO(getHistories(count, offset, threadId, order,lastMessageId,firstMessageId))) {
                        messageDao.deleteMessageWithFirstMessageIdASC(count, offset, threadId, firstMessageId);
                    }

                } else if (messageVOS.size() == 1) {
                    if (getHistories(count, offset, threadId, order,lastMessageId,firstMessageId).size() > 1) {
                        messageDao.deleteMessageWithFirstMessageIdASC(count, offset, threadId, firstMessageId);
                        saveMessage(cMessageVOS.get(0), threadId);
                    }

                } else if (messageVOS.size() > 1) {
                    int size = getHistories(count, offset, threadId, order,lastMessageId,firstMessageId).size();
                    long firstMesssageId = getHistories(count, offset, threadId, order,lastMessageId,firstMessageId).get(0).getId();
                    long lastMesssageId = getHistories(count, offset, threadId, order,lastMessageId,firstMessageId).get(size - 1).getId();
                    messageDao.deleteMessageBetweenLastAndFirstASC(threadId, firstMesssageId, lastMesssageId);
                    saveHistory(cMessageVOS, threadId);
                }
            } else if (!Util.isNullOrEmpty(callback.getLastMessageId())) {
                if (Util.isNullOrEmptyMessageVO(messageVOS)) {
                    if (!Util.isNullOrEmptyMessageVO(getHistories(count, offset, threadId, order,lastMessageId,firstMessageId))) {
                        messageDao.deleteMessageWithFirstMessageIdASC(count, offset, threadId, firstMessageId);
                    }

                } else if (messageVOS.size() == 1) {
                    if (getHistories(count, offset, threadId, order,lastMessageId,firstMessageId).size() > 1) {
                        messageDao.deleteMessageWithFirstMessageIdASC(count, offset, threadId, firstMessageId);
                        saveMessage(cMessageVOS.get(0), threadId);
                    }

                } else if (messageVOS.size() > 1) {
                    int size = getHistories(count, offset, threadId, order,lastMessageId,firstMessageId).size();
                    long firstMesssageId = getHistories(count, offset, threadId, order,lastMessageId,firstMessageId).get(0).getId();
                    long lastMesssageId = getHistories(count, offset, threadId, order,lastMessageId,firstMessageId).get(size - 1).getId();
                    messageDao.deleteMessageBetweenLastAndFirstASC(threadId, firstMesssageId, lastMesssageId);
                    saveHistory(cMessageVOS, threadId);
                }


            }



        }


//        if (chatMessage.getContent().isEmpty()) {
//            if (!Util.isNullOrEmptyMessageVO(messageDatabaseHelper.getHistories(callback.getCount(), callback.getOffset(), chatMessage.getSubjectId(), callback.getOrder()))) {
//                messageDatabaseHelper.deleteMessageAfterOffset(callback.getCount(), callback.getOffset(), chatMessage.getSubjectId(), callback.getOrder());
//            }
//        }
//
//        if (messageVOS.size() == 1) {
//            if ((messageDatabaseHelper.getHistories(callback.getCount(), callback.getOffset(), chatMessage.getSubjectId(), callback.getOrder()).size() > 1)) {
//
//            }
//        }
    }

    public List<MessageVO> getHistories(long count, long offset, long threadId, String order, long lastMessageId, long firstMessageId) {
        List<MessageVO> messageVOS = new ArrayList<>();
        List<CacheMessageVO> cMessageVOS = new ArrayList<>();

        if (lastMessageId > 0 && firstMessageId > 0) {
            if (order.equals("asc")) {
                messageDao.getHistoriesFandLASC(count, offset, threadId, firstMessageId, lastMessageId);
            } else {
                messageDao.getHistoriesFandLDESC(count, offset, threadId, firstMessageId, lastMessageId);
            }
        } else if (firstMessageId > 0) {
            if (order.equals("asc")) {
                messageDao.getHistoriesMessageIdASC(count, offset, threadId, firstMessageId);
            } else {
                messageDao.getHistoriesMessageIdDESC(count, offset, threadId, firstMessageId);
            }
        } else if (lastMessageId > 0) {
            if (order.equals("asc")) {
                messageDao.getHistoriesMessageIdASC(count, offset, threadId, firstMessageId);
            } else {
                messageDao.getHistoriesMessageIdDESC(count, offset, threadId, firstMessageId);
            }
        } else  {

            if (order.equals("asc")) {
                if (messageDao.getHistoriesASC(count, offset, threadId) != null && messageDao.getHistoriesASC(count, offset, threadId).size() > 0) {
                    cMessageVOS = messageDao.getHistoriesASC(count, offset, threadId);
                }

            } else {
                if (messageDao.getHistoriesDESC(count, offset, threadId) != null && messageDao.getHistoriesDESC(count, offset, threadId).size() > 0) {
                    cMessageVOS = messageDao.getHistoriesDESC(count, offset, threadId);
                }
            }
        }


        Participant participant = null;
        ReplyInfoVO replyInfoVO = null;
        ForwardInfo forwardInfo = null;
        Thread thread = null;
        ConversationSummery conversationSummery = null;
        for (CacheMessageVO cacheMessageVO : cMessageVOS) {
            if (!Util.isNullOrEmpty(cacheMessageVO.getConversationId())) {
                cacheMessageVO.setConversation(messageDao.getThreadById(cacheMessageVO.getConversationId()));
                ThreadVo threadVo = cacheMessageVO.getConversation();
                thread = new Thread(
                        threadVo.getId(),
                        threadVo.getJoinDate(),
                        threadVo.getInviter(),
                        null,
                        threadVo.getTitle(),
                        null,
                        threadVo.getTime(),
                        threadVo.getLastMessage(),
                        threadVo.getLastParticipantName(),
                        threadVo.getLastParticipantImage(),
                        threadVo.isGroup(),
                        threadVo.getPartner(),
                        threadVo.getImage(),
                        threadVo.getDescription(),
                        threadVo.getUnreadCount(),
                        threadVo.getLastSeenMessageId(),
                        threadVo.getPartnerLastMessageId(),
                        threadVo.getPartnerLastSeenMessageId(),
                        threadVo.getPartnerLastDeliveredMessageId(),
                        threadVo.getType(),
                        threadVo.isMute(),
                        threadVo.getMetadata(),
                        threadVo.isCanEditInfo(),
                        threadVo.getParticipantCount(),
                        threadVo.isCanSpam(),
                        threadVo.isAdmin()
                );
            }
            if (cacheMessageVO.getForwardInfoId() != null) {
                cacheMessageVO.setForwardInfo(messageDao.getForwardInfo(cacheMessageVO.getForwardInfoId()));
            }
            if (cacheMessageVO.getParticipantId() != null) {
                CacheParticipant cacheParticipant = messageDao.getParticipant(cacheMessageVO.getParticipantId());
                participant = new Participant(
                        cacheParticipant.getId(),
                        cacheParticipant.getName(),
                        cacheParticipant.getFirstName(),
                        cacheParticipant.getLastName(),
                        cacheParticipant.getImage(),
                        cacheParticipant.getNotSeenDuration(),
                        cacheParticipant.getContactId(),
                        cacheParticipant.getContactName(),
                        cacheParticipant.getContactFirstName(),
                        cacheParticipant.getContactLastName(),
                        cacheParticipant.getSendEnable(),
                        cacheParticipant.getReceiveEnable(),
                        cacheParticipant.getCellphoneNumber(),
                        cacheParticipant.getEmail(),
                        cacheParticipant.getMyFriend(),
                        cacheParticipant.getOnline(),
                        cacheParticipant.getBlocked(),
                        cacheParticipant.getAdmin()
                );

            }
            if (cacheMessageVO.getReplyInfoVOId() != null) {
                CacheReplyInfoVO cacheReplyInfoVO = messageDao.getReplyInfo(cacheMessageVO.getReplyInfoVOId());
                replyInfoVO = new ReplyInfoVO(
                        cacheReplyInfoVO.getRepliedToMessageId(),
                        cacheReplyInfoVO.getMessageType(),
                        cacheReplyInfoVO.isDeleted(),
                        cacheReplyInfoVO.getRepliedToMessage(),
                        cacheReplyInfoVO.getSystemMetadata(),
                        cacheReplyInfoVO.getMetadata(),
                        cacheReplyInfoVO.getMessage()
                );
            }
            if (cacheMessageVO.getForwardInfo() != null) {
                CacheForwardInfo cacheForwardInfo = messageDao.getForwardInfo(cacheMessageVO.getForwardInfoId());
                if (cacheForwardInfo.getParticipantId() != null) {
                    CacheParticipant cacheParticipant = messageDao.getParticipant(cacheForwardInfo.getParticipantId());
                    participant = new Participant(
                            cacheParticipant.getId(),
                            cacheParticipant.getName(),
                            cacheParticipant.getFirstName(),
                            cacheParticipant.getLastName(),
                            cacheParticipant.getImage(),
                            cacheParticipant.getNotSeenDuration(),
                            cacheParticipant.getContactId(),
                            cacheParticipant.getContactName(),
                            cacheParticipant.getContactFirstName(),
                            cacheParticipant.getContactLastName(),
                            cacheParticipant.getSendEnable(),
                            cacheParticipant.getReceiveEnable(),
                            cacheParticipant.getCellphoneNumber(),
                            cacheParticipant.getEmail(),
                            cacheParticipant.getMyFriend(),
                            cacheParticipant.getOnline(),
                            cacheParticipant.getBlocked(),
                            cacheParticipant.getAdmin()
                    );
                }
                if (Util.isNullOrEmpty(cacheForwardInfo.getConversationId())) {
                    conversationSummery = messageDao.getConversationSummery(cacheForwardInfo.getConversationId());
                }
                forwardInfo = new ForwardInfo(participant, conversationSummery);
            }

            MessageVO messageVO = new MessageVO(
                    cacheMessageVO.getId(),
                    cacheMessageVO.isEdited(),
                    cacheMessageVO.isEditable(),
                    cacheMessageVO.isDelivered(),
                    cacheMessageVO.isSeen(),
                    cacheMessageVO.getUniqueId(),
                    cacheMessageVO.getMessageType(),
                    cacheMessageVO.getPreviousId(),
                    cacheMessageVO.getMessage(),
                    participant,
                    cacheMessageVO.getTime(),
                    cacheMessageVO.getMetadata(),
                    cacheMessageVO.getSystemMetadata(),
                    thread,
                    replyInfoVO,
                    forwardInfo
            );

            messageVOS.add(messageVO);
        }

        return messageVOS;
    }


    public long getHistoryContentCount() {
        return messageDao.getHistoryCount();
    }

    //Cache contact
    public List<Contact> getContacts() {
        return messageDao.getContact();
    }

    public void saveContacts(List<Contact> contacts) {
        messageDao.insertContacts(contacts);
    }

    public void saveContact(Contact contact) {
        messageDao.insertContact(contact);
    }

    public void saveUserInfo(UserInfo userInfo) {
        messageDao.insertUserInfo(userInfo);
    }

    public UserInfo getUserInfo() {
        return messageDao.getUserInfo();
    }

    public int getThreadCount() {
        return messageDao.getThreadCount();
    }

    public List<Thread> getThreads(long count, long offset) {
        List<Thread> threads;
        if (messageDao.getThreads(count, offset) != null) {
            List<ThreadVo> threadVos = messageDao.getThreads(count, offset);
            threads = new ArrayList<>();
            CacheParticipant cacheParticipant;
            CacheReplyInfoVO cacheReplyInfoVO;
            Participant participant = null;
            ReplyInfoVO replyInfoVO = null;
            for (ThreadVo threadVo : threadVos) {
                LastMessageVO lastMessageVO = null;
                if (threadVo.getInviterId() != null) {
                    threadVo.setInviter(messageDao.getInviter(threadVo.getInviterId()));
                }
                if (threadVo.getLastMessageVOId() != null) {
                    threadVo.setLastMessageVO(messageDao.getLastMessageVO(threadVo.getLastMessageVOId()));
                    CacheLastMessageVO cacheLastMessageVO = threadVo.getLastMessageVO();
                    if (cacheLastMessageVO.getParticipantId() != null) {
                        cacheParticipant = messageDao.getParticipant(cacheLastMessageVO.getParticipantId());
                        participant = new Participant(
                                cacheParticipant.getId(),
                                cacheParticipant.getName(),
                                cacheParticipant.getFirstName(),
                                cacheParticipant.getLastName(),
                                cacheParticipant.getImage(),
                                cacheParticipant.getNotSeenDuration(),
                                cacheParticipant.getContactId(),
                                cacheParticipant.getContactName(),
                                cacheParticipant.getContactFirstName(),
                                cacheParticipant.getContactLastName(),
                                cacheParticipant.getSendEnable(),
                                cacheParticipant.getReceiveEnable(),
                                cacheParticipant.getCellphoneNumber(),
                                cacheParticipant.getEmail(),
                                cacheParticipant.getMyFriend(),
                                cacheParticipant.getOnline(),
                                cacheParticipant.getBlocked(),
                                cacheParticipant.getAdmin()
                        );
                    }
                    if (cacheLastMessageVO.getReplyInfoVOId() != null) {
                        cacheReplyInfoVO = messageDao.getReplyInfo(cacheLastMessageVO.getReplyInfoVOId());
                        replyInfoVO = new ReplyInfoVO(
                                cacheReplyInfoVO.getRepliedToMessageId(),
                                cacheReplyInfoVO.getMessageType(),
                                cacheReplyInfoVO.isDeleted(),
                                cacheReplyInfoVO.getRepliedToMessage(),
                                cacheReplyInfoVO.getSystemMetadata(),
                                cacheReplyInfoVO.getMetadata(),
                                cacheReplyInfoVO.getMessage()
                        );
                    }

                    if (cacheLastMessageVO.getReplyInfoVOId() != null) {

                    }

                    lastMessageVO = new LastMessageVO(
                            cacheLastMessageVO.getId(),
                            cacheLastMessageVO.getUniqueId(),
                            cacheLastMessageVO.getMessage(),
                            cacheLastMessageVO.isEdited(),
                            cacheLastMessageVO.isEditable(),
                            cacheLastMessageVO.getTime(),
                            participant,
                            replyInfoVO,
                            null
                    );
                }


                Thread thread = new Thread(
                        threadVo.getId(),
                        threadVo.getJoinDate(),
                        threadVo.getInviter(),
                        lastMessageVO,
                        threadVo.getTitle(),
                        null,
                        threadVo.getTime(),
                        threadVo.getLastMessage(),
                        threadVo.getLastParticipantName(),
                        threadVo.getLastParticipantImage(),
                        threadVo.isGroup(),
                        threadVo.getPartner(),
                        threadVo.getImage(),
                        threadVo.getDescription(),
                        threadVo.getUnreadCount(),
                        threadVo.getLastSeenMessageId(),
                        threadVo.getPartnerLastMessageId(),
                        threadVo.getPartnerLastSeenMessageId(),
                        threadVo.getPartnerLastDeliveredMessageId(),
                        threadVo.getType(),
                        threadVo.isMute(),
                        threadVo.getMetadata(),
                        threadVo.isCanEditInfo(),
                        threadVo.getParticipantCount(),
                        threadVo.isCanSpam(),
                        threadVo.isAdmin()
                );
                threads.add(thread);
            }
            return threads;
        } else {
            threads = new ArrayList<>();
        }

        return threads;
    }

    public List<Thread> getThreadsByThreadName(String threadName) {
        List<Thread> threads;
        if (messageDao.getThreadByName(threadName) != null) {
            ThreadVo threadVo = messageDao.getThreadByName(threadName);
            threads = new ArrayList<>();
            CacheParticipant cacheParticipant;
            CacheReplyInfoVO cacheReplyInfoVO;
            Participant participant = null;
            ReplyInfoVO replyInfoVO = null;
            LastMessageVO lastMessageVO = null;
            if (threadVo.getInviterId() != null) {
                threadVo.setInviter(messageDao.getInviter(threadVo.getInviterId()));
            }
            if (threadVo.getLastMessageVOId() != null) {
                threadVo.setLastMessageVO(messageDao.getLastMessageVO(threadVo.getLastMessageVOId()));
                CacheLastMessageVO cacheLastMessageVO = threadVo.getLastMessageVO();
                if (cacheLastMessageVO.getParticipantId() != null) {
                    cacheParticipant = messageDao.getParticipant(cacheLastMessageVO.getParticipantId());
                    participant = new Participant(
                            cacheParticipant.getId(),
                            cacheParticipant.getName(),
                            cacheParticipant.getFirstName(),
                            cacheParticipant.getLastName(),
                            cacheParticipant.getImage(),
                            cacheParticipant.getNotSeenDuration(),
                            cacheParticipant.getContactId(),
                            cacheParticipant.getContactName(),
                            cacheParticipant.getContactFirstName(),
                            cacheParticipant.getContactLastName(),
                            cacheParticipant.getSendEnable(),
                            cacheParticipant.getReceiveEnable(),
                            cacheParticipant.getCellphoneNumber(),
                            cacheParticipant.getEmail(),
                            cacheParticipant.getMyFriend(),
                            cacheParticipant.getOnline(),
                            cacheParticipant.getBlocked(),
                            cacheParticipant.getAdmin()

                    );
                }
                if (cacheLastMessageVO.getReplyInfoVOId() != null) {
                    cacheReplyInfoVO = messageDao.getReplyInfo(cacheLastMessageVO.getReplyInfoVOId());
                    replyInfoVO = new ReplyInfoVO(
                            cacheReplyInfoVO.getRepliedToMessageId(),
                            cacheReplyInfoVO.getMessageType(),
                            cacheReplyInfoVO.isDeleted(),
                            cacheReplyInfoVO.getRepliedToMessage(),
                            cacheReplyInfoVO.getSystemMetadata(),
                            cacheReplyInfoVO.getMetadata(),
                            cacheReplyInfoVO.getMessage()
                    );
                }

                if (cacheLastMessageVO.getReplyInfoVOId() != null) {

                }

                lastMessageVO = new LastMessageVO(
                        cacheLastMessageVO.getId(),
                        cacheLastMessageVO.getUniqueId(),
                        cacheLastMessageVO.getMessage(),
                        cacheLastMessageVO.isEdited(),
                        cacheLastMessageVO.isEditable(),
                        cacheLastMessageVO.getTime(),
                        participant,
                        replyInfoVO,
                        null
                );
            }

            Thread thread = new Thread(
                    threadVo.getId(),
                    threadVo.getJoinDate(),
                    threadVo.getInviter(),
                    lastMessageVO,
                    threadVo.getTitle(),
                    null,
                    threadVo.getTime(),
                    threadVo.getLastMessage(),
                    threadVo.getLastParticipantName(),
                    threadVo.getLastParticipantImage(),
                    threadVo.isGroup(),
                    threadVo.getPartner(),
                    threadVo.getImage(),
                    threadVo.getDescription(),
                    threadVo.getUnreadCount(),
                    threadVo.getLastSeenMessageId(),
                    threadVo.getPartnerLastMessageId(),
                    threadVo.getPartnerLastSeenMessageId(),
                    threadVo.getPartnerLastDeliveredMessageId(),
                    threadVo.getType(),
                    threadVo.isMute(),
                    threadVo.getMetadata(),
                    threadVo.isCanEditInfo(),
                    threadVo.getParticipantCount(),
                    threadVo.isCanSpam(),
                    threadVo.isAdmin()
            );
            threads.add(thread);
        } else {
            return new ArrayList<>();
        }

        return threads;
    }

    public List<Thread> getThreadsByThreadIds(ArrayList<Integer> threadIds) {
        List<Thread> threads = new ArrayList<>();

        for (int id : threadIds) {
            if (messageDao.getThreadById(id) != null) {
                ThreadVo threadVo = messageDao.getThreadById(id);
                CacheParticipant cacheParticipant;
                CacheReplyInfoVO cacheReplyInfoVO;
                Participant participant = null;
                ReplyInfoVO replyInfoVO = null;
                LastMessageVO lastMessageVO = null;
                if (threadVo.getInviterId() != null) {
                    threadVo.setInviter(messageDao.getInviter(threadVo.getInviterId()));
                }
                if (threadVo.getLastMessageVOId() != null) {
                    threadVo.setLastMessageVO(messageDao.getLastMessageVO(threadVo.getLastMessageVOId()));
                    CacheLastMessageVO cacheLastMessageVO = threadVo.getLastMessageVO();
                    if (cacheLastMessageVO.getParticipantId() != null) {
                        cacheParticipant = messageDao.getParticipant(cacheLastMessageVO.getParticipantId());
                        participant = new Participant(
                                cacheParticipant.getId(),
                                cacheParticipant.getName(),
                                cacheParticipant.getFirstName(),
                                cacheParticipant.getLastName(),
                                cacheParticipant.getImage(),
                                cacheParticipant.getNotSeenDuration(),
                                cacheParticipant.getContactId(),
                                cacheParticipant.getContactName(),
                                cacheParticipant.getContactFirstName(),
                                cacheParticipant.getContactLastName(),
                                cacheParticipant.getSendEnable(),
                                cacheParticipant.getReceiveEnable(),
                                cacheParticipant.getCellphoneNumber(),
                                cacheParticipant.getEmail(),
                                cacheParticipant.getMyFriend(),
                                cacheParticipant.getOnline(),
                                cacheParticipant.getBlocked(),
                                cacheParticipant.getAdmin()
                        );
                    }
                    if (cacheLastMessageVO.getReplyInfoVOId() != null) {
                        cacheReplyInfoVO = messageDao.getReplyInfo(cacheLastMessageVO.getReplyInfoVOId());
                        replyInfoVO = new ReplyInfoVO(
                                cacheReplyInfoVO.getRepliedToMessageId(),
                                cacheReplyInfoVO.getMessageType(),
                                cacheReplyInfoVO.isDeleted(),
                                cacheReplyInfoVO.getRepliedToMessage(),
                                cacheReplyInfoVO.getSystemMetadata(),
                                cacheReplyInfoVO.getMetadata(),
                                cacheReplyInfoVO.getMessage()
                        );
                    }

                    if (cacheLastMessageVO.getReplyInfoVOId() != null) {

                    }

                    lastMessageVO = new LastMessageVO(
                            cacheLastMessageVO.getId(),
                            cacheLastMessageVO.getUniqueId(),
                            cacheLastMessageVO.getMessage(),
                            cacheLastMessageVO.isEdited(),
                            cacheLastMessageVO.isEditable(),
                            cacheLastMessageVO.getTime(),
                            participant,
                            replyInfoVO,
                            null
                    );
                }


                Thread thread = new Thread(
                        threadVo.getId(),
                        threadVo.getJoinDate(),
                        threadVo.getInviter(),
                        lastMessageVO,
                        threadVo.getTitle(),
                        null,
                        threadVo.getTime(),
                        threadVo.getLastMessage(),
                        threadVo.getLastParticipantName(),
                        threadVo.getLastParticipantImage(),
                        threadVo.isGroup(),
                        threadVo.getPartner(),
                        threadVo.getImage(),
                        threadVo.getDescription(),
                        threadVo.getUnreadCount(),
                        threadVo.getLastSeenMessageId(),
                        threadVo.getPartnerLastMessageId(),
                        threadVo.getPartnerLastSeenMessageId(),
                        threadVo.getPartnerLastDeliveredMessageId(),
                        threadVo.getType(),
                        threadVo.isMute(),
                        threadVo.getMetadata(),
                        threadVo.isCanEditInfo(),
                        threadVo.getParticipantCount(),
                        threadVo.isCanSpam(),
                        threadVo.isAdmin()
                );
                threads.add(thread);
            }
        }

        return threads;
    }

    public void saveThreads(List<ThreadVo> threadVos) {
        try {
            CacheLastMessageVO cacheLastMessageVO = null;
            CacheReplyInfoVO cacheReplyInfoVO;
            CacheForwardInfo cacheForwardInfo;
            for (ThreadVo threadVo : threadVos) {
                if (threadVo.getInviter() != null) {

                    threadVo.setInviterId(threadVo.getInviter().getId());
                    messageDao.insertInviter(threadVo.getInviter());
                }
                if (threadVo.getLastMessageVO() != null) {

                    threadVo.setLastMessageVOId(threadVo.getLastMessageVO().getId());
                    cacheLastMessageVO = threadVo.getLastMessageVO();
                    messageDao.insertLastMessageVO(cacheLastMessageVO);
                    if (threadVo.getLastMessageVO().getParticipant() != null) {

                        cacheLastMessageVO.setParticipantId(threadVo.getLastMessageVO().getParticipant().getId());
                        messageDao.insertLastMessageVO(cacheLastMessageVO);
                        messageDao.insertParticipant(threadVo.getLastMessageVO().getParticipant());
                    }
                    if (threadVo.getLastMessageVO().getReplyInfoVO() != null) {
                        cacheReplyInfoVO = threadVo.getLastMessageVO().getReplyInfoVO();
                        cacheLastMessageVO.setReplyInfoVOId(threadVo.getLastMessageVO().getReplyInfoVO().getId());
                        messageDao.insertLastMessageVO(cacheLastMessageVO);
                        messageDao.insertReplyInfoVO(cacheReplyInfoVO);

                        if (threadVo.getLastMessageVO().getReplyInfoVO().getParticipant() != null) {

                            cacheReplyInfoVO.setParticipantId(threadVo.getLastMessageVO().getReplyInfoVO().getParticipant().getId());
                            messageDao.insertReplyInfoVO(cacheReplyInfoVO);
                            messageDao.insertParticipant(threadVo.getLastMessageVO().getReplyInfoVO().getParticipant());
                        }
                    }
                    if (threadVo.getLastMessageVO().getForwardInfo() != null) {

                        cacheForwardInfo = threadVo.getLastMessageVO().getForwardInfo();
                        cacheForwardInfo.setId(threadVo.getLastMessageVO().getId());
                        messageDao.insertForwardInfo(cacheForwardInfo);
                        cacheLastMessageVO.setForwardInfoId(threadVo.getLastMessageVO().getId());
                        messageDao.insertLastMessageVO(cacheLastMessageVO);
                        if (threadVo.getLastMessageVO().getForwardInfo().getParticipant() != null) {

                            cacheForwardInfo.setParticipantId(threadVo.getLastMessageVO().getForwardInfo().getParticipant().getId());
                            messageDao.insertParticipant(threadVo.getLastMessageVO().getForwardInfo().getParticipant());
                        }
                        if (threadVo.getLastMessageVO().getForwardInfo().getConversation() != null) {
                            cacheForwardInfo.setConversationId(threadVo.getLastMessageVO().getForwardInfo().getConversation().getId());
                            messageDao.insertConversationSummery(threadVo.getLastMessageVO().getForwardInfo().getConversation());
                        }
                    }
                }
                messageDao.insertThread(threadVo);
            }
        } catch (Exception e) {

        }

    }

    public void saveParticipants(List<CacheParticipant> participants, long threadId) {
        for (CacheParticipant participant : participants) {
            participant.setThreadId(threadId);
            messageDao.insertParticipant(participant);
        }
    }

    public void saveParticipant(CacheParticipant cacheParticipant) {
        messageDao.insertParticipant(cacheParticipant);
    }

    public List<Participant> getThreadParticipant(long offset, long count, long threadId) {
        List<Participant> participants = new ArrayList<>();
        if (messageDao.geParticipants(offset, count, threadId) == null) {
            return participants;
        } else {
            List<CacheParticipant> cacheParticipants = messageDao.geParticipants(offset, count, threadId);

            for (CacheParticipant cParticipant : cacheParticipants) {
                Participant participant = new Participant(
                        cParticipant.getId(),
                        cParticipant.getName(),
                        cParticipant.getFirstName(),
                        cParticipant.getLastName(),
                        cParticipant.getImage(),
                        cParticipant.getNotSeenDuration(),
                        cParticipant.getContactId(),
                        cParticipant.getContactName(),
                        cParticipant.getContactFirstName(),
                        cParticipant.getContactLastName(),
                        cParticipant.getSendEnable(),
                        cParticipant.getReceiveEnable(),
                        cParticipant.getCellphoneNumber(),
                        cParticipant.getEmail(),
                        cParticipant.getMyFriend(),
                        cParticipant.getOnline(),
                        cParticipant.getBlocked(),
                        cParticipant.getAdmin()
                );
                participants.add(participant);
            }
        }
        return participants;
    }

    public long getParticipantCount(long threadId) {
        return messageDao.getParticipantCount(threadId);
    }

    //Cache contact
    public Contact getContactById(long id) {
        return messageDao.getContactById(id);
    }

    public List<Contact> getContactsByFirst(String firstName) {
        return messageDao.getContactsByFirst(firstName);
    }

    public List<Contact> getContactsByLast(String lastName) {
        return messageDao.getContactsByLast(lastName);
    }

    public List<Contact> getContactsByFirstAndLast(String firstName, String lastName) {
        return messageDao.getContactsByFirstAndLast(firstName, lastName);
    }

    public List<Contact> getContactByCell(String cellphoneNumber) {
        return messageDao.getContactByCell(cellphoneNumber);
    }

    public List<Contact> getContactsByEmail(String email) {
        return messageDao.getContactsByEmail(email);
    }

}
