package com.tziegler.keyboard;

public class Key {
	char main = ' '; 
	char shifted = ' '; 
	
	Key(char m) {
		main = m; 
	}
	
	Key(char m, char s) {
		main = m;
		shifted = s; 
	}
	
	public void setMainChar(char c) {
		main = c; 
	}
	
	public void setShifted(char c) {
		shifted = c; 
	}
	
	public char getMainChar() {
		return main;
	}
	
	public char getShifted() {
		return shifted;
	}
	
	public boolean hasShift() {
		if (shifted != ' ') 
			return true;
		return false;
	}
	
	public boolean isSymbol() {
		return !(main >= 'a' && main <= 'z'); 
	}
	
	public Key clone() {
		return new Key(main, shifted); 
	}
}
