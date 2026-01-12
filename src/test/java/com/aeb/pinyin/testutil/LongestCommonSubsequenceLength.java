package com.aeb.pinyin.testutil;

import java.util.List;
import java.util.function.BiPredicate;

public class LongestCommonSubsequenceLength<T> {
	
	public LongestCommonSubsequenceLength(List<T> seq1, List<T> seq2, BiPredicate<T, T> equalityComparator) {
		this.seq1 = seq1;
		this.seq2 = seq2;
		this.equalityComparator = equalityComparator;
		xDim = seq1.size();
		yDim = seq2.size();
		lcsTable = new int[xDim * yDim];
	}
	
	private final List<T> seq1;
	private final List<T> seq2;
	private final BiPredicate<T, T> equalityComparator;
	
	private final int xDim;
	private final int yDim;
	
	private final int[] lcsTable;
	
	public int computeLongestCommonSubsequenceLength() {
		for (int y = 0; y < yDim; y++) {
			for (int x = 0; x < xDim; x++) {
				int cellResult;
				if (equalityComparator.test(seq1.get(x), seq2.get(y))) {
					cellResult = get(x - 1, y - 1) + 1;
				} else {
					int upper = get(x, y - 1);
					int left = get(x - 1, y);
					cellResult = Math.max(upper, left);
				}
				set(x, y, cellResult);
			}
		}
		return get(xDim - 1, yDim - 1);
	}
	
	private int get(int x, int y) {
		if (x == -1 || y == -1) {
			return 0;
		}
		return lcsTable[index(x, y)];
	}
	
	private void set(int x, int y, int value) {
		lcsTable[index(x, y)] = value;
	}
	
	private int index(int x, int y) {
		return y * xDim + x;
	}
	
}
