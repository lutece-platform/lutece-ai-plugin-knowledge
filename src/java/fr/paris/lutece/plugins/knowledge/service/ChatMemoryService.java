package fr.paris.lutece.plugins.knowledge.service;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import fr.paris.lutece.plugins.knowledge.business.Bot;
import fr.paris.lutece.plugins.knowledge.business.BotSession;
import fr.paris.lutece.plugins.knowledge.business.BotSessionHome;
import fr.paris.lutece.plugins.knowledge.rs.RequestData;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;



import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatMemoryService
{
    private static final String ATTRIBUTE_CHAT_MEMORY_MAP = "CHAT_MEMORY_MAP";
    
    /**
     * Retrieves a chat memory associated with the given project ID from the provided session. If no chat memory exists for the given project ID, a new one is
     * created.
     * 
     * @param session
     *            The current HTTP session.
     * @param projectId
     *            The ID of the project for which the chat memory is to be fetched.
     * @return The associated MessageWindowChatMemory object or a new one if none exists.
     */
    @SuppressWarnings( "unchecked" )
    public static Pair<PersistentChatMemoryStore, String> getChatMemory(HttpServletRequest request, RequestData data, Bot bot, String sessionId ) {
        LuteceUser luteceUser = SecurityService.getInstance().getRegisteredUser(request);
        HttpSession session = request.getSession();
         PersistentChatMemoryStore chatMemoryStore = null;
    
        Map<String, PersistentChatMemoryStore> chatMemoryStoreMap = (Map<String, PersistentChatMemoryStore>) session.getAttribute(ATTRIBUTE_CHAT_MEMORY_MAP);
    
        if (chatMemoryStoreMap == null) {
            chatMemoryStoreMap = new HashMap<>();
            session.setAttribute(ATTRIBUTE_CHAT_MEMORY_MAP, chatMemoryStoreMap);
        }


        if( data.getBotSessionId() != null ) {
            chatMemoryStore = chatMemoryStoreMap.get( data.getBotSessionId() );
        }

        String botSessionId;

        if (chatMemoryStore == null) {
            if (data.getBotSessionId() != null) {
                BotSession botSession = BotSessionHome.findByAccessCode( luteceUser.getAccessCode(), data.getBotSessionId() ).get();
                chatMemoryStore = new PersistentChatMemoryStore();
                chatMemoryStore.updateMessages(botSession.getSessionId(), messagesFromJson(botSession.getContent()));
                chatMemoryStoreMap.put( botSession.getSessionId() , chatMemoryStore);
                botSessionId = botSession.getSessionId();
            } else {
                BotSession botSession = createNewBotSession(bot, sessionId, luteceUser );
                chatMemoryStore = new PersistentChatMemoryStore();
                chatMemoryStoreMap.put(sessionId, chatMemoryStore);
                botSessionId = botSession.getSessionId();
            }
        } else {
            botSessionId = data.getBotSessionId();
        }
    
        return new ImmutablePair<>(chatMemoryStore, botSessionId);
    }

    private static BotSession createNewBotSession(Bot bot, String sessionId, LuteceUser luteceUser ) {
        long timestamp = System.currentTimeMillis();
        BotSession botSession = new BotSession();
        botSession.setCreationDate(new java.sql.Date(timestamp));
        botSession.setBotId(bot.getId());
        botSession.setAccessCode( luteceUser.getAccessCode( ) );
        botSession.setSessionId( sessionId );
        return BotSessionHome.create(botSession);
    }

      static class PersistentChatMemoryStore implements ChatMemoryStore {
        @Override
        public List<ChatMessage> getMessages(Object memoryId) {

        BotSession botSession = BotSessionHome.findBySessionId( (String) memoryId ).get( );
        return messagesFromJson( botSession.getContent( ) );
        }

        @Override
        public void updateMessages(Object memoryId, List<ChatMessage> messages) {

            BotSession botSession = BotSessionHome.findBySessionId( (String) memoryId ).get( );
            botSession.setContent( messagesToJson( messages ) );
            BotSessionHome.update( botSession );
        }

        @Override
        public void deleteMessages(Object memoryId) {
            BotSessionHome.remove( (int) memoryId );
        }
    }

}
