package com.lapins.bookmarktagger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by alapins on 7/14/2017.
 */
public class App {
    public static void main(String[] args) {
        File f = new File(args[0]);
        File f2 = new File(args[1]);
        new App().run(f, f2);
    }

    private ArrayList<String> stack = new ArrayList<>();

    private void run(File f, File out) {
        try {
            Document doc = Jsoup.parse(f, "UTF-8", "http://localhost");
            Elements body = doc.body().children();
            body.forEach(this::process);
            FileWriter fw = new FileWriter(out);
            fw.write(body.html());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(Element e) {
        if (e.tagName().equals("dl")) {
            e.children().forEach(this::process);
        } else if (e.tagName().equals("dt")) {
            Element firstChild = e.child(0);
            if (firstChild.tagName().equals("h3")) {
                stack.add(firstChild.text());
                int idx = stack.size() - 1;
                for (int i = 1; i < e.children().size(); i++) {
                    process(e.child(i));
                }
                stack.remove(idx);
            } else if (firstChild.tagName().equals("a")) {
                firstChild.attr("PRIVATE", "1");
                firstChild.attr("TAGS", stack.stream().collect(Collectors.joining(",")));
            }
        }
    }
}
