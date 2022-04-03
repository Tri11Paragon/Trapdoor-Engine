package com.trapdoor.engine.tools;

/**
 * @author brett
 * @date Apr. 3, 2022
 * 
 */
public class BUtil {
	
	public static void printOutBits(int bits) {
		String line = "Bits: ";
		for (int i = 0; i < 32; i++) {
			int bit = (bits >> i) & 0x1;
			line += bit;
			if ((i+1) % 4 == 0)
				line += " ";
		}
		Logging.logger.trace(line);
	}
	
}
