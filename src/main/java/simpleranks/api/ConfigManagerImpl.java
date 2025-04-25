package simpleranks.api;

import simpleranks.utils.config.DefaultConfiguration;

public class ConfigManagerImpl implements ConfigManager {

    @Override
    public void setDefaultRank(String rankName) {
        DefaultConfiguration.defaultRank.set(rankName);
    }

    @Override
    public String getDefaultRank() {
        return DefaultConfiguration.defaultRank.get();
    }

    @Override
    public void setChatRankFormat(String format) {
        DefaultConfiguration.chatRankFormat.set(format);
    }

    @Override
    public String getChatFormat() {
        return DefaultConfiguration.chatRankFormat.get();
    }

    @Override
    public void setChatRankEnabled(boolean enabled) {
        DefaultConfiguration.chatRankEnabled.set(enabled);
    }

    @Override
    public boolean getChatEnabled() {
        return DefaultConfiguration.chatRankEnabled.get();
    }

    @Override
    public void setTeamRankSeparator(String separator) {
        DefaultConfiguration.teamRankSeparator.set(separator);
    }

    @Override
    public String getTeamRankSeparator() {
        return DefaultConfiguration.teamRankSeparator.get();
    }

    @Override
    public void setTeamRankEnabled(boolean enabled) {
        DefaultConfiguration.teamRankEnabled.set(enabled);
    }

    @Override
    public boolean getTeamRankEnabled() {
        return DefaultConfiguration.teamRankEnabled.get();
    }

    @Override
    public void setTeamRankPlayerNameColor(String color) {
        DefaultConfiguration.teamRankPlayerNameColor.set(color);
    }

    @Override
    public String getTeamRankPlayerNameColor() {
        return DefaultConfiguration.teamRankPlayerNameColor.get();
    }

    @Override
    public void setJoinMessageFormat(String format) {
        DefaultConfiguration.joinMessageFormat.set(format);
    }

    @Override
    public String getJoinMessageFormat() {
        return DefaultConfiguration.joinMessageFormat.get();
    }

    @Override
    public void setQuitMessageFormat(String format) {
        DefaultConfiguration.quitMessageFormat.set(format);
    }

    @Override
    public String getQuitMessageFormat() {
        return DefaultConfiguration.quitMessageFormat.get();
    }

    @Override
    public void setJoinMessageEnabled(boolean enabled) {
        DefaultConfiguration.joinMessageEnabled.set(enabled);
    }

    @Override
    public boolean getJoinMessageEnabled() {
        return DefaultConfiguration.joinMessageEnabled.get();
    }

    @Override
    public void setQuitMessageEnabled(boolean enabled) {
        DefaultConfiguration.quitMessageEnabled.set(enabled);
    }

    @Override
    public boolean getQuitMessageEnabled() {
        return DefaultConfiguration.quitMessageEnabled.get();
    }

    @Override
    public void setRankTimerEnabled(boolean enabled) {
        DefaultConfiguration.rankTimerEnabled.set(enabled);
    }

    @Override
    public boolean getRankTimerEnabled() {
        return DefaultConfiguration.rankTimerEnabled.get();
    }

    @Override
    public void setDefaultPermissionGroup(String group) {
        DefaultConfiguration.defaultPermissionGroup.set(group);
    }

    @Override
    public String getDefaultPermissionGroup() {
        return DefaultConfiguration.defaultPermissionGroup.get();
    }
}
