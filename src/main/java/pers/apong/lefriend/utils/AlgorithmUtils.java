package pers.apong.lefriend.utils;


import java.util.List;
import java.util.Objects;

/**
 * 算法工具类
 */
public class AlgorithmUtils {

    /**
     * 编辑距离算法
     * 用于计算标签之间的距离值 d
     * 相似度 = 1 / d
     *
     * @param tags1
     * @param tags2
     * @return
     */
    public static int minDistance(String tags1, String tags2){
        int n = tags1.length();
        int m = tags2.length();

        if(n * m == 0) {
            return n + m;
        }

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++){
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++){
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++){
            for (int j = 1; j < m + 1; j++){
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int left_down = d[i - 1][j - 1];
                if (!Objects.equals(tags1.charAt(i-1), tags2.charAt(j - 1)))
                    left_down += 1;
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }
}
