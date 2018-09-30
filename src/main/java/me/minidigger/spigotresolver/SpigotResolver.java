package me.minidigger.spigotresolver;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.util.Pair;

/**
 * Created by Martin on 15-Apr-17.
 */
public class SpigotResolver {

    Gson gson = new Gson();
    Date date = new Date();

    public static void main(String[] args) throws Exception {
        new SpigotResolver().resolve();
    }

    public void resolve() throws Exception {
        // get all versions
        List<String> files = getFiles();

        List<Info> infos = new CopyOnWriteArrayList<>();

        // get the info for those versions
        files.parallelStream().forEach(v -> {
            try {
                Info info = getInfo(v);
                if (info.version.contains("pre")) {
                    System.out.println("Skipping snapshot " + info.version);
                } else {
                    infos.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // sort based of semver
        infos.sort(Comparator.comparing((Info a) -> Integer.parseInt(a.version.split("\\.")[1])).thenComparing((Info a) -> {
            String ver = a.version.split("\\.")[2];
            return ver.equals("json") ? 0 : Integer.parseInt(ver);
        }));

        // generate bbcode
        List<String> page = generatePage(infos);

        // print it all
        page.forEach(System.out::println);
    }

    private List<String> generatePage(List<Info> infos) {
        List<String> result = new ArrayList<>();
        result.add("This page is automatically generated using this tool by MiniDigger: [URL='https://github.com/MiniDigger/spigot-resolver']Spigot Resolver[/URL]");
        result.add("If the page is outdated go nag MiniDigger or just update it yourself using that tool");
        result.add("This page contains all versions of spigot you can build using buildtools (java -jar BuildTools.jar --rev <version>), together with the nms and bukkit (maven) version and links to the sources on stash for that version.");
        result.add("");
        result.add("[LIST]");
        for (Info info : infos) {
            result.add("[*]" + info.version.replace(".json", ""));
            result.add("[LIST]");
            result.add("[*]NMS Version: " + info.nmsVersion);
            result.add("[*]Bukkit Version: " + info.bukkitVersion);
            result.add("[*][URL='" + info.bukkitLink + "']Bukkit Link[/URL]");
            result.add("[*][URL='" + info.craftbukkitLink + "']CraftBukkit Link[/URL]");
            result.add("[*][URL='" + info.spigotLink + "']Spigot Link[/URL]");
            result.add("[*][URL='" + info.buildDataLink + "']BuildData Link[/URL]");
            result.add("[*][/LIST]");
        }
        result.add("[/LIST]");
        String ms = new Date().getTime() - date.getTime() + "";
        result.add("Generated on " + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()).replace("T", " ") + " in " + ms + " ms.");
        return result;
    }

    private List<String> getFiles() throws Exception {
        URL url = new URL("https://hub.spigotmc.org/versions/");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        List<String> files = new ArrayList<>();

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.startsWith("<a href")) {
                String version = inputLine.split("\"")[1];
                // contains . in name
                if (version.indexOf(".") < version.lastIndexOf(".")) {
                    files.add(version);
                }
            }
        }
        in.close();

        return files;
    }

    private Info getInfo(String version) throws Exception {
        URL url = new URL("https://hub.spigotmc.org/versions/" + version);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        Version ver = gson.fromJson(in, Version.class);
        in.close();

        Info info = new Info();
        info.version = version;

        info.buildDataLink = "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/browse?at=" + ver.refs.BuildData;
        info.bukkitLink = "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse?at=" + ver.refs.Bukkit;
        info.craftbukkitLink = "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse?at=" + ver.refs.CraftBukkit;
        info.spigotLink = "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/spigot/browse?at=" + ver.refs.Spigot;

        // get nms and bukkit version from pom
        Pair<String, String> pom = resolvePom(ver.refs.CraftBukkit);
        info.nmsVersion = pom.getKey();
        info.bukkitVersion = pom.getValue();

        return info;
    }

    private Pair<String, String> resolvePom(String craftbukkitCommit) throws Exception {
        URL url = new URL("https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/raw/pom.xml?at=" + craftbukkitCommit);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String inputLine;
        String nmsVersion = "ERROR";
        String cbVersion = "ERROR";
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.contains("<version>")) {
                cbVersion = inputLine.split("[><]")[2];
            } else if (inputLine.contains("minecraft_version")) {
                nmsVersion = inputLine.split("[><]")[2];
                break;
            }
        }

        return new Pair<>(nmsVersion, cbVersion);
    }

    class Version {
        String name;
        String description;
        Refs refs;
        int toolsVersion;

        @Override
        public String toString() {
            return "Version{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", refs=" + refs +
                    ", toolsVersion=" + toolsVersion +
                    '}';
        }

        class Refs {
            String BuildData;
            String Bukkit;
            String CraftBukkit;
            String Spigot;

            @Override
            public String toString() {
                return "Refs{" +
                        "BuildData='" + BuildData + '\'' +
                        ", Bukkit='" + Bukkit + '\'' +
                        ", CraftBukkit='" + CraftBukkit + '\'' +
                        ", Spigot='" + Spigot + '\'' +
                        '}';
            }
        }
    }

    class Info {
        String version;
        String nmsVersion;
        String bukkitVersion;
        String buildDataLink;
        String craftbukkitLink;
        String bukkitLink;
        String spigotLink;

        @Override
        public String toString() {
            return "Info{" +
                    "version='" + version + '\'' +
                    ", nmsVersion='" + nmsVersion + '\'' +
                    ", buildDataLink='" + buildDataLink + '\'' +
                    ", craftbukkitLink='" + craftbukkitLink + '\'' +
                    ", bukkitLink='" + bukkitLink + '\'' +
                    ", spigotLink='" + spigotLink + '\'' +
                    '}';
        }
    }
}
