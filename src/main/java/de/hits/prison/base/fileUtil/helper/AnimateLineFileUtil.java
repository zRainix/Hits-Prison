package de.hits.prison.base.fileUtil.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AnimateLineFileUtil extends FileUtil {

    public AnimateLineFileUtil(File file) {
        super(file);
    }

    public AnimateLineFileUtil(String fileName) {
        super(fileName);
    }

    public void setAnimatedLinesList(String path, List<AnimatedLines> list) {
        setAnimatedLinesList(path, list, false);
    }

    public void setAnimatedLinesList(String path, List<AnimatedLines> list, boolean saveDefault) {
        if (!saveDefault)
            cfg.set(path, null);

        List<Map<String, List<Map<String, Object>>>> all = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            AnimatedLines lines = list.get(i);
            List<Map<String, Object>> serializedLines = new ArrayList<>();
            for (AnimatedLine line : lines.getAnimatedLines()) {
                serializedLines.add(line.toMap());
            }
            all.add(Map.of("Line", serializedLines));
        }
        if (saveDefault)
            cfg.addDefault(path, all);
        else
            cfg.set(path, all);
    }

    public List<AnimatedLines> getAnimatedLinesList(String path) {
        List<Map<?, ?>> all = cfg.getMapList(path);
        List<AnimatedLines> resultList = new ArrayList<>();
        if (all.isEmpty()) {
            return cfg.getStringList(path).stream().map(line -> new AnimatedLines(0, List.of(new AnimatedLine(line)))).collect(Collectors.toList());
        }
        for (Map<?, ?> linesMap : all) {
            List<Map<String, Object>> serializedLines = (List<Map<String, Object>>) linesMap.get("Line");
            if (serializedLines != null) {
                List<AnimatedLine> animatedLines = new ArrayList<>();
                for (Map<String, Object> lineMap : serializedLines) {
                    animatedLines.add(AnimatedLine.fromMap(lineMap));
                }
                resultList.add(new AnimatedLines(0, animatedLines));
            }
        }
        return resultList;
    }

    public static class AnimatedLines {

        int currentIndex;
        List<AnimatedLine> animatedLines;

        public AnimatedLines(int currentIndex, List<AnimatedLine> animatedLines) {
            this.currentIndex = currentIndex;
            this.animatedLines = animatedLines;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public void setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        public AnimatedLine getCurrentLine() {
            return animatedLines.get(currentIndex%animatedLines.size());
        }

        public AnimatedLine update() {
            AnimatedLine line = getCurrentLine();
            if (!line.next())
                return line;

            this.currentIndex++;
            this.currentIndex %= animatedLines.size();

            return getCurrentLine();
        }

        public List<AnimatedLine> getAnimatedLines() {
            return animatedLines;
        }

        public void setAnimatedLines(List<AnimatedLine> animatedLines) {
            this.animatedLines = animatedLines;
        }
    }

    public static class AnimatedLine {

        String text;
        long duration;
        long current = 0;

        public AnimatedLine(String text) {
            this.text = text;
            this.duration = 1;
        }

        public AnimatedLine(String text, long duration) {
            this.text = text;
            this.duration = duration;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public long getCurrent() {
            return current;
        }

        public void setCurrent(long current) {
            this.current = current;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("text", text);
            if (duration != 1)
                map.put("duration", duration);
            return map;
        }

        public static AnimatedLine fromMap(Map<String, Object> map) {
            String text = (String) map.get("text");
            long delay = (Integer) map.getOrDefault("duration", 1);
            return new AnimatedLine(text, delay);
        }

        public boolean next() {
            if (this.duration < 0)
                return false;

            this.current++;
            if (this.current >= this.duration) {
                this.current = 0;
                return true;
            }
            return false;
        }
    }
}
