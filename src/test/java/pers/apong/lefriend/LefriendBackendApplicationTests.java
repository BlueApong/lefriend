package pers.apong.lefriend;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.ToIntFunction;


class LefriendBackendApplicationTests {

    @Test
    void contextLoads() throws NoSuchMethodException {
        int[] a = new int[]{101,3,3,101};
        List<Integer> list = new ArrayList<>();
        for(int n:a){
            list.add(n);
        }
        System.out.println(check(list));
    }

    public boolean check(List<?> list){
        for(int i=0,j=list.size()-1; i<j; i++, j--){
            if(!list.get(i).equals(list.get(j))){
                return false;
            }
        }
        return true;
    }
}
