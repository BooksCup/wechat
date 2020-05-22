package com.bc.wechat.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.style.ImageSpan;

import com.bc.wechat.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiUtil {
    public static final String emoji_1 = "[):]";
    public static final String emoji_2 = "[:D]";
    public static final String emoji_3 = "[;)]";
    public static final String emoji_4 = "[:-o]";
    public static final String emoji_5 = "[:p]";
    public static final String emoji_6 = "[(H)]";
    public static final String emoji_7 = "[:@]";
    public static final String emoji_8 = "[:s]";
    public static final String emoji_9 = "[:$]";
    public static final String emoji_10 = "[:(]";
    public static final String emoji_11 = "[:'(]";
    public static final String emoji_12 = "[:|]";
    public static final String emoji_13 = "[(a)]";
    public static final String emoji_14 = "[8o|]";
    public static final String emoji_15 = "[8-|]";
    public static final String emoji_16 = "[+o(]";
    public static final String emoji_17 = "[<o)]";
    public static final String emoji_18 = "[|-)]";
    public static final String emoji_19 = "[*-)]";
    public static final String emoji_20 = "[:-#]";
    public static final String emoji_21 = "[:-*]";
    public static final String emoji_22 = "[^o)]";
    public static final String emoji_23 = "[8-)]";
    public static final String emoji_24 = "[(|)]";
    public static final String emoji_25 = "[(u)]";
    public static final String emoji_26 = "[(S)]";
    public static final String emoji_27 = "[(*)]";
    public static final String emoji_28 = "[(#)]";
    public static final String emoji_29 = "[(R)]";
    public static final String emoji_30 = "[({)]";
    public static final String emoji_31 = "[(})]";
    public static final String emoji_32 = "[(k)]";
    public static final String emoji_33 = "[(F)]";
    public static final String emoji_34 = "[(W)]";
    public static final String emoji_35 = "[(D)]";

    private static final Spannable.Factory spannableFactory = Spannable.Factory
            .getInstance();

    private static final Map<Pattern, Integer> emoticons = new HashMap<>();

    static {

        addPattern(emoticons, emoji_1, R.drawable.emoji_01);
        addPattern(emoticons, emoji_2, R.drawable.emoji_02);
        addPattern(emoticons, emoji_3, R.drawable.emoji_03);
        addPattern(emoticons, emoji_4, R.drawable.emoji_04);
        addPattern(emoticons, emoji_5, R.drawable.emoji_05);
        addPattern(emoticons, emoji_6, R.drawable.emoji_06);
        addPattern(emoticons, emoji_7, R.drawable.emoji_07);
        addPattern(emoticons, emoji_8, R.drawable.emoji_08);
        addPattern(emoticons, emoji_9, R.drawable.emoji_09);
        addPattern(emoticons, emoji_10, R.drawable.emoji_10);
        addPattern(emoticons, emoji_11, R.drawable.emoji_11);
        addPattern(emoticons, emoji_12, R.drawable.emoji_12);
        addPattern(emoticons, emoji_13, R.drawable.emoji_13);
        addPattern(emoticons, emoji_14, R.drawable.emoji_14);
        addPattern(emoticons, emoji_15, R.drawable.emoji_15);
        addPattern(emoticons, emoji_16, R.drawable.emoji_16);
        addPattern(emoticons, emoji_17, R.drawable.emoji_17);
        addPattern(emoticons, emoji_18, R.drawable.emoji_18);
        addPattern(emoticons, emoji_19, R.drawable.emoji_19);
        addPattern(emoticons, emoji_20, R.drawable.emoji_20);
        addPattern(emoticons, emoji_21, R.drawable.emoji_21);
        addPattern(emoticons, emoji_22, R.drawable.emoji_22);
        addPattern(emoticons, emoji_23, R.drawable.emoji_23);
        addPattern(emoticons, emoji_24, R.drawable.emoji_24);
        addPattern(emoticons, emoji_25, R.drawable.emoji_25);
        addPattern(emoticons, emoji_26, R.drawable.emoji_26);
        addPattern(emoticons, emoji_27, R.drawable.emoji_27);
        addPattern(emoticons, emoji_28, R.drawable.emoji_28);
        addPattern(emoticons, emoji_29, R.drawable.emoji_29);
        addPattern(emoticons, emoji_30, R.drawable.emoji_30);
        addPattern(emoticons, emoji_31, R.drawable.emoji_31);
        addPattern(emoticons, emoji_32, R.drawable.emoji_32);
        addPattern(emoticons, emoji_33, R.drawable.emoji_33);
        addPattern(emoticons, emoji_34, R.drawable.emoji_34);
        addPattern(emoticons, emoji_35, R.drawable.emoji_35);
    }

    private static void addPattern(Map<Pattern, Integer> map, String smile,
                                   int resource) {
        map.put(Pattern.compile(Pattern.quote(smile)), resource);
    }

    /**
     * replace existing spannable with smiles
     *
     * @param context
     * @param spannable
     * @return
     */
    public static boolean addEmojis(Context context, Spannable spannable) {
        boolean hasChanges = false;
        for (Map.Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class)) {
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end()) {
                        spannable.removeSpan(span);
                    } else {
                        set = false;
                        break;
                    }
                }
                if (set) {
                    hasChanges = true;
                    spannable.setSpan(new ImageSpan(context, entry.getValue()),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    public static Spannable getEmojisText(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addEmojis(context, spannable);
        return spannable;
    }

    public static boolean containsKey(String key) {
        boolean b = false;
        for (Map.Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(key);
            if (matcher.find()) {
                b = true;
                break;
            }
        }

        return b;
    }
}
