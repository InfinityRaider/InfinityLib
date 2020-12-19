package com.infinityraider.infinitylib.utility;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class Combinatorics {
    public static Combinations get(int n) {
        return new Combinations(n);
    }

    public static <T extends Enum<T>> List<T>[][] getForEnum(Class<T> clazz) {
        T[] enums = clazz.getEnumConstants();
        return getAsObject(enums.length, i -> enums[i]);
    }

    public static <T> List<T>[][] getAsObject(int n, IntFunction<T> mapper) {
        return get(n).map(mapper);
    }

    public static <T> List<T>[] combineAsObject(int n, Function<int[], T> mapper) {
        return get(n).map(mapper);
    }

    public static final class Combinations {
        private final int n;
        private final int[][][] combinations;

        private Combinations(int n) {
            this.n = n;
            this.combinations = new int[n + 1][][];
            this.fillMap();
        }

        public int[][] getCombinationsForAmount(int amount) {
            return this.combinations[amount];
        }

        @SuppressWarnings("unchecked")
        public <T> List<T>[][] map(IntFunction<T> mapper) {
            List<T>[][] out = new List[this.combinations.length][];
            for(int i = 0; i < this.combinations.length; i++) {
                out[i] = new List[this.combinations[i].length];
                for(int j = 0; j < this.combinations[i].length; j++) {
                    out[i][j] = Arrays.stream(this.combinations[i][j]).mapToObj(mapper).collect(Collectors.toList());
                }
            }
            return out;
        }

        @SuppressWarnings("unchecked")
        public <T> List<T>[] map(Function<int[], T> mapper) {
            List<T>[] out = new List[this.combinations.length];
            for(int i = 0; i < this.combinations.length; i++) {
                out[i] = Arrays.stream(this.combinations[i]).map(mapper).collect(Collectors.toList());
            }
            return out;
        }

        private void fillMap() {
            for(int i = 0; i < this.combinations.length; i++) {
                this.combinations[i] = this.determineCombinationsForAmount(i);
            }
        }

        private int[][] determineCombinationsForAmount(int amount) {
            int[][] combinations = new int[this.getCombinationCount(amount)][];
            int[] reference = new int[amount];
            for(int combinationIndex = 0; combinationIndex < combinations.length; combinationIndex++) {
                int[] combination = new int[amount];
                if(combinationIndex == 0) {
                    for(int i = 0; i < amount; i++) {
                        combination[i] = i;
                        reference[i] = i;
                    }
                    if(amount > 0) {
                        reference[amount - 1] = reference[amount - 1] + 1;
                    }
                } else {
                    int index = amount - 1;
                    for(int i = 0; i < amount; i++) {
                        combination[i] = reference[i];
                    }
                    int counter = 1;
                    boolean flag = combination[index] >= (this.n - counter);
                    while(flag && index > 0) {
                        counter++;
                        index = index - 1;
                        flag = reference[index] >= (this.n - counter);
                    }
                    reference[index] = reference[index] + 1;
                    for(int i = index + 1; i < amount; i++) {
                        reference[i] = reference[i - 1] + 1;
                    }
                }
                combinations[combinationIndex] = combination;
            }
            return combinations;
        }

        private int getCombinationCount(int i) {
            if(i == 0) {
                return 1;
            }
            return (getCombinationCount(i - 1) * (this.n - i + 1))/i;
        }
    }
}
