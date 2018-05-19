import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bailey
 * @date 2018/5/12
 */
public class Test {
    public static void main(String... args) {
        //        String pattern    = "/sub/\\w+\\.wlq/(\\w+)";

        String origUrlPattern = "/sub/{name}/*.wlq/{id}";
        String urlRegex        = origUrlPattern.replaceAll("\\*", "\\\\w+").replaceAll("\\.", "\\\\.").replaceAll("\\{\\w+\\}", "(\\\\w+)");
        String paramRegex      = "\\{(\\w+)\\}";

        String url = "/sub/zhangsan/some.wlq/2";

        Pattern paramPattern = Pattern.compile(paramRegex);
        Matcher paramMatcher = paramPattern.matcher(origUrlPattern);
        int     paramCount   = 0;
        while (paramMatcher.find()) {
            for (int i = 1, count = paramMatcher.groupCount(); i <= count; i = i + 2) {
                System.out.println("Found Param " + (paramCount++) + ": " + paramMatcher.group(i));
            }
        }
        System.out.println("Total Param: " + paramCount);

        System.out.println();
        System.out.println("origUrlPattern = " + origUrlPattern);
        System.out.println("url = " + url);
        System.out.println("urlRegex = " + urlRegex);

        Pattern urlPattern = Pattern.compile(urlRegex);
        Matcher matcherUrl = urlPattern.matcher(url);

        boolean isUrlMatch = matcherUrl.find();
        if (isUrlMatch) {
            int valueCount = 0;
            do {
                for (int i = 1, count = matcherUrl.groupCount(); i <= count; i = i + 1) {
                    System.out.println("Found value " + (valueCount++) + ": " + matcherUrl.group(i));
                }
            } while (matcherUrl.find());
            System.out.println("Total value: " + valueCount);
        } else {
            System.out.println("URL NOT MATCH!!!");
        }
    }
}
