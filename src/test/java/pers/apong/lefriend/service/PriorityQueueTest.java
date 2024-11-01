package pers.apong.lefriend.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.PriorityQueue;

@SpringBootTest
public class PriorityQueueTest {
    @Test
    void testAdd() {
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(5);
        priorityQueue.add(4);
        priorityQueue.add(3);
        priorityQueue.add(2);
        priorityQueue.add(1);
        System.out.println(Arrays.toString(priorityQueue.toArray()));
        for (int i = 0; i < priorityQueue.size(); i++) {
            Integer poll = priorityQueue.poll();
            System.out.println(poll);
        }
    }
}
