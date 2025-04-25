package simpleranks.api;

public interface ConfigManager {

    void setDefaultRank(String rankName);
    String getDefaultRank();

    void setChatRankFormat(String format);
    String getChatFormat();

    void setChatRankEnabled(boolean enabled);
    boolean getChatEnabled();

    void setTeamRankSeparator(String separator);
    String getTeamRankSeparator();

    void setTeamRankEnabled(boolean enabled);
    boolean getTeamRankEnabled();

    void setTeamRankPlayerNameColor(String color);
    String getTeamRankPlayerNameColor();

    void setJoinMessageFormat(String format);
    String getJoinMessageFormat();

    void setQuitMessageFormat(String format);
    String getQuitMessageFormat();

    void setJoinMessageEnabled(boolean enabled);
    boolean getJoinMessageEnabled();

    void setQuitMessageEnabled(boolean enabled);
    boolean getQuitMessageEnabled();

    void setRankTimerEnabled(boolean enabled);
    boolean getRankTimerEnabled();

    void setDefaultPermissionGroup(String group);
    String getDefaultPermissionGroup();

}
