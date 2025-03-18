package com.hyunwns.demoweb.animal;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;

@Slf4j
public class TreeMapTest {

    @Test
    public void treeMapTest() throws Exception{

        Map<Integer, String> a = new TreeMap<>();
        a.put(1, "ABC");

        Map<Integer, String> b = new TreeMap<>();
        a.put(3, "DEF");

        Map<Integer, String> c = new TreeMap<>();
        a.put(2, "GHI");

        Map<Integer, String> map = new TreeMap<>(Comparator.reverseOrder());

        map.putAll(a); map.putAll(b); map.putAll(c);

        for (String s : map.values()) {
            log.info(s);
        }
    }

    @Test
    public void TESTA() throws Exception{
        //given
        Map<Integer, List<Object>> t = new TreeMap<>(Comparator.reverseOrder());

        //when
        for (int i = 0; i < 20; i++) {
            t.put(i, new ArrayList<>());

            for (int j = 0; j < 20; j++) {
                t.get(i).add(j + "TEST");
            }
        }

        int size = 0;
        for(List<Object> o : t.values()) {
            size += o.size();
        }
        log.info("size: {}", size);

        for(List<Object> o : t.values()) {
            Collections.reverse(o);
            for (Object k : o) {
                log.info("{}", k);
            }
        }

        //then
    }
}
