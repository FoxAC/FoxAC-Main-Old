package dev.isnow.fox.util;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@UtilityClass
public class CrasherUtils {
    public boolean isInvalidBookTag(NBTTagCompound tag) {
        if(tag == null)
            return false;

        NBTTagList pagesNBTList = tag.getList("pages", 8);
        ArrayList<String> pages = Lists.newArrayList();

        for(int i = 0; i < pagesNBTList.size(); i++) {
            pages.add(pagesNBTList.getString(i));
        }

        int pageLimit = 50;
        int pageByteLimit = 750;
        int byteLimit = 38000;
        int maxAllowedChars = 300;

        if(pages.size() > pageLimit) {
            return true;
        }

        StringBuilder builder = new StringBuilder();
        int bookContentBytes = 0;
        for(String page : pages) {
            int pageBytes = page.getBytes(StandardCharsets.UTF_8).length;
            bookContentBytes += pageBytes;

            if(pageBytes > pageByteLimit) {
                return true;
            }

            builder.append(page);
        }
        if(bookContentBytes > byteLimit) {
            return true;
        }
        String bookContent = builder.toString();
        char[] chars = bookContent.toCharArray();

        maxAllowedChars *= pages.size();

        if(chars.length > maxAllowedChars) {
            return true;
        }

        return false;
    }

}
