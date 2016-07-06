package skychat;

import java.util.regex.Pattern;

public class Formatting
{
    private static final Pattern chatColorPattern = Pattern.compile("(?i)&([0-9A-F])");
    private static final Pattern chatMagicPattern = Pattern.compile("(?i)&([K])");
    private static final Pattern chatBoldPattern = Pattern.compile("(?i)&([L])");
    private static final Pattern chatStrikethroughPattern = Pattern.compile("(?i)&([M])");
    private static final Pattern chatUnderlinePattern = Pattern.compile("(?i)&([N])");
    private static final Pattern chatItalicPattern = Pattern.compile("(?i)&([O])");
    private static final Pattern chatResetPattern = Pattern.compile("(?i)&([R])");
    public static String FormatStringColor(String tobeformatted) {
        String allFormated = chatColorPattern.matcher(tobeformatted).replaceAll("§$1");
        allFormated = allFormated.replaceAll("%", "\\%");
        return allFormated;
    }

    public static String FormatStringMagic(String tobeformatted) {
        String allFormated = chatMagicPattern.matcher(tobeformatted).replaceAll("§$1");
        allFormated = chatBoldPattern.matcher(allFormated).replaceAll("§$1");
        allFormated = chatStrikethroughPattern.matcher(allFormated).replaceAll("§$1");
        allFormated = chatUnderlinePattern.matcher(allFormated).replaceAll("§$1");
        allFormated = chatItalicPattern.matcher(allFormated).replaceAll("§$1");
        allFormated = chatResetPattern.matcher(allFormated).replaceAll("§$1");
        allFormated = allFormated.replaceAll("%", "\\%");
        return allFormated;
    }

    public static String FormatPlayerName(String playerPrefix, String playerDisplayName, String playerSuffix) {
        playerPrefix = chatColorPattern.matcher(playerPrefix).replaceAll("§$1");
        playerPrefix = chatMagicPattern.matcher(playerPrefix).replaceAll("§$1");
        playerPrefix = chatBoldPattern.matcher(playerPrefix).replaceAll("§$1");
        playerPrefix = chatStrikethroughPattern.matcher(playerPrefix).replaceAll("§$1");
        playerPrefix = chatUnderlinePattern.matcher(playerPrefix).replaceAll("§$1");
        playerPrefix = chatItalicPattern.matcher(playerPrefix).replaceAll("§$1");
        playerPrefix = chatResetPattern.matcher(playerPrefix).replaceAll("§$1");
        playerSuffix = chatColorPattern.matcher(playerSuffix).replaceAll("§$1");
        playerSuffix = chatMagicPattern.matcher(playerSuffix).replaceAll("§$1");
        playerSuffix = chatBoldPattern.matcher(playerSuffix).replaceAll("§$1");
        playerSuffix = chatStrikethroughPattern.matcher(playerSuffix).replaceAll("§$1");
        playerSuffix = chatUnderlinePattern.matcher(playerSuffix).replaceAll("§$1");
        playerSuffix = chatItalicPattern.matcher(playerSuffix).replaceAll("§$1");
        playerSuffix = chatResetPattern.matcher(playerSuffix).replaceAll("§$1");
        return playerPrefix + playerDisplayName.trim() + playerSuffix;
    }

    public static String FormatStringAll(String tobeformatted) {
        String allFormated = chatColorPattern.matcher(tobeformatted).replaceAll("§$1");
        allFormated = chatMagicPattern.matcher(allFormated).replaceAll("§$1");
        allFormated = chatBoldPattern.matcher(allFormated).replaceAll("§$1");
        allFormated = chatStrikethroughPattern.matcher(allFormated).replaceAll("§$1");
        allFormated = chatUnderlinePattern.matcher(allFormated).replaceAll("§$1");
        allFormated = chatItalicPattern.matcher(allFormated).replaceAll("§$1");
        allFormated = chatResetPattern.matcher(allFormated).replaceAll("§$1");
        allFormated = allFormated.replaceAll("%", "\\%");
        return allFormated;
    }
}
