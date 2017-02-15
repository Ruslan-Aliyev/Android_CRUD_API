package com.ruslan_website.travelblog.utils.common;

import java.io.File;

public class PathCombiner {
    public static String combine(String part1, String part2){
        File combined = new File(part1, part2);
        return combined.getPath();
    }
}
