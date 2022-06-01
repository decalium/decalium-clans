package org.gepron1x.test;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToStringHelperTest {

    private static final String BOOBS = """
                       <span style="color:#8d1c57">▓</span><span style="color:#b72275">ÑÑ▒▓▓▒Æ▀        </span><span style="color:#7b655d">Y,      </span><span style="color:#90786b">░         ;;    ░░░▒░░░░░░░U░░░░░▄▀░░░░░░╙%▄▄▄@▓</span><span style="color:#6e1a42">▓</span><span style="color:#6c1e42">▓▓▓▓▒▓</span><br><span style="color:#73415a">▀╜</span><span></span><span style="color:#755361">""╙`             *u     ░       ▓▓M▀▓▓▄░░░░░░░░░░░░░░░aÜ░░░░░░░░░░░▀▓▓▒▓▓▓▓▒▓▒</span><br><span style="color:#6d544b">▀%wµ             ░░░░`W,   ░      ╚µ`╚▀▒</span><span style="color:#64203d">█</span><span style="color:#4c201b">▓▓</span><span style="color:#9f6d5e">░</span><span style="color:#a26f5e">░░░░░░░░░░░Ñ ░░░░░░░░░░░░░░░</span><span style="color:#7c3144">▓▒Ñ</span><span style="color:#811b4e">▓▓▒▓▒</span><br><span style="color:#8a6659">m</span><span style="color:#987361">░░░`*Vµ,, ░░░        ░░▒H, ░      └¥ ,µÄ▀░░░░░░░░░░µÅ.░░░░░░░░░░░░░░░░░░▀</span><span style="color:#6c1d41">▓</span><span style="color:#95265e">▒▀╪▓▓</span><br><span style="color:#906353">m░░</span><span style="color:#9b6f60">░░░░░`*R▄░░░        ░░░░▒@,    ░░░░░░░░░░░░░░░░▄╩ ░░░░░░░       ░░░░▒╝"`    `</span><br><span style="color:#94604f">▒░░░░░░░░░░░`"*èµ░░░░░░░░░░░▒</span><span style="color:#7b3d30">▓▓▄  ░▒▒░░░░░░░µ▄@▓▀░░░░░░░░░           ▒ ,µ╗▒  :</span><br><span style="color:#985c4e">▒░░░░▒░░</span><span style="color:#a06b5a">░░░░░░░░░░╙¥▄░░░░░░░░▒▓▓▓▓▄¡░,▄▄▒▒▒▒▒ñ░░░░░░░▒░   y░        ░U░b░∩  ░</span><br><span style="color:#906152">░░░░░░░░░░░░░▒░░░░░░░░░▒¥╤▒░░░░▒▓</span><span style="color:#7b3e2f">▓▓▓▓▓</span><span style="color:#592821">▌▒▒▒╝╙</span><span style="color:#977164">░</span><span style="color:#9d6d5d">░░░░░▒▒░    ░┼▒░░      ┼:▒▒`  ░</span><br><span style="color:#926454">m░░░░░░░░░░░░░░░░░░░░▒░░░░░▀▄▒░░░▒▒▒▓</span><span style="color:#54231e">█▒▒▀ ░░░░░░▒░       ░░</span><span style="color:#522e2c">▌</span><span style="color:#756762">Ü </span><span style="color:#8f7769">░    ░░▒▒   ░</span><br><span style="color:#916151">▒░░░░░░░░░░░░░░░░░░░░░░░░░▒░░╙%▒▒░░░</span><span style="color:#76433a">4▓╩ ░▒░░▒░░         ░░]Ñ :   ░░░░▒   ░</span><br><span style="color:#683d36">▌░░░░░░░▒▒╩</span><span style="color:#8d726a">"</span><span style="color:#8c7870">``        ```"ª▒▒░░  ░░▒</span><span style="color:#5d352e">▓Ü ░▒░░      ░░░ u░░░░</span><span style="color:#583f3a">▓      </span><span style="color:#9b7160">░</span><span style="color:#a06e5e">░░░Ü  ░</span><br><span style="color:#633d36">▀▒▒▒▒▒`                 ░░░░░`*y¿   ' ░░      ░       `%▄▄       ░░░▒</span><br><span style="color:#8d6254">░▓▒▓░                 </span><span style="color:#88796e">:░░░░░░░░░`Yφ░        ░            ▀⌂░    ░░░░▒  ░░      ░</span><br><span style="color:#916354">m╙</span><span style="color:#67362f">▌░                 ░░░░░░░░░░░░░░Å▒░░     ░     ░░░░░░░░└U░▒░ ░░░░░U   ░  ░░░░</span><br><span style="color:#926354">m</span><span style="color:#a06f5c">░▀░              ░░░░░░░░░░░░░░░░░░]▒░░░    ░  ░░      ░░░╙▄▒░ ░░░░░▒     ░░░░░</span><br><span style="color:#916455">m░░</span><span style="color:#613b35">▓░            ╔░░░░░▒░░░░░░░░░░░░░▓░░▒░              ░░░░]▒░░░░░░░░▒   ░ ░░░░</span><br><span style="color:#8b6657">m</span><span style="color:#9e6f5e">░░▐▄░        ░░░░░░░░▒░░░░░░░░░░░░░░┼░░░▒░       ░░░░░░▒░░░░▌░░░░░░░░▒    ░░░░░</span><br><span style="color:#896759">m░░░▀n░   ,░░  ░░░░░▒░░░░░░░░░░░░░░░░▄▒░▒▄▄A▒Ö▒▒▒%W▄░░░░░▒▒░▒</span><span style="color:#633632">▌░░░░µ▄A*╜▒▒▒Mµ ░░░</span><br><span style="color:#89695b">∩</span><span style="color:#987362">░░░░▀░       :░░░░░░░░░░░░░░░░░░░░░▄▌*"`░░░░░░░░░░░▀░░░░░░░▐▒▒▄╧"    ░░░░░░"W⌂░</span><br><span style="color:#886c5c">∩░░░░░</span><span style="color:#653832">▓▒      ░░░░░░░░░░░░░░░░░░▄A"└     ░░░░░░░░░░░░Å▄░░░▒▒▓Ä`       ░░░░░░░░░▒</span><br><span style="color:#84766e">'</span><span style="color:#8c7b72">`'`"`"╙'      '"'''''''''""\"'"`         `''""````````""``"▀""\""        ``''"'""</span><br><span style="color:#808080"> </span><br><span style="color:#808080"> </span><br><span style="color:#808080"> </span><br><span style="color:#808080"> </span><br><span style="color:#808080"> </span><br><span style="color:#808080"> </span><br><span style="color:#808080"> </span><br><span style="color:#808080"> </span><br><span style="color:#808080"> </span><br><br>
            """;


    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("(<span style=\"color:(#.{6})\">)");
        Matcher matcher = pattern.matcher(BOOBS);
        String str = BOOBS;
        while(matcher.find()) {
            System.out.println(matcher.group(2));
            str = str.replace(matcher.group(1), "<color:'"+matcher.group(2)+"'>");
        }
        str = str.replace("</span>", "</color>");
        System.out.println(str);
    }
}
