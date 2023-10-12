package fr.paris.lutece.plugins.knowledge.rs;

public class RequestData
{
    private String action;
    private String question;
    private String botId;
    private String botSessionId;
    private byte [ ] audioFile;

    public String getAction( )
    {
        return action;
    }

    public void setAction( String action )
    {
        this.action = action;
    }

    public String getQuestion( )
    {
        return question;
    }

    public void setQuestion( String question )
    {
        this.question = question;
    }

    public String getBotId( )
    {
        return botId;
    }

    public void setBotId( String botId )
    {
        this.botId = botId;
    }

    public byte [ ] getAudioFile( )
    {
        return audioFile;
    }

    public void setAudioFile( byte [ ] audioFile )
    {
        this.audioFile = audioFile;
    }

    public String getBotSessionId( )
    {
        return botSessionId;
    }

    public void setBotSessionId( String botSessionId )
    {
        this.botSessionId = botSessionId;
    }

}
