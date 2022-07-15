package it.pixel.grouping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class main {

    private record Dto(String a, String b, String c) {
    }


    public static void main(String[] args) {

        record Dto(String a, String b, String c) {
        }



        List<Dto> dtos = List.of(new Dto("a", "a", "a"), new Dto("b", "b", "b"));

        GroupingHandler<Dto> groupingHandler = new GroupingHandler(dtos, "a");

        groupingHandler.getGroupedList()
                .stream()
                .sorted()
                .skip().limit().collect()
    }

}
