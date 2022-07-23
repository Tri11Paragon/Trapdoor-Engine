package com.trapdoor.totalcrafter.data.nbt;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;

import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Jul. 8, 2022
 * 
 */
public class TagWriter extends DataOutputStream {

	/**
	 * RAM Speeds:
	 * bs=8192 		| 14.9 GB/s
	 * bs=16384 	| 19.0 GB/s
	 * bs=32768		| 21.3 GB/s
	 * bs=65536		| 23.2 GB/s
	 * bs=131072	| 24.1 GB/s
	 * bs=262144	| 24.5 GB/s
	 * bs=524288	| 24.3 GB/s
	 *
	 * SSD Speeds:
	 * bs=8192		| 388 MB/s
	 * bs=16384		| 572 MB/s | 396 MB/s
	 * bs=32768		| 498 MB/s
	 * bs=65536		| 468 MB/s
	 * bs=131072	| 476 MB/s
	 * bs=262144	| 463 MB/s
	 * bs=524288	| 473 MB/s
	 * 
	 */
	private static final int BUFFER_SIZE = 8192;
	
	public TagWriter(String file) throws Exception {
		super(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE)));
	}
	
//	public TagWriter(String file, Compression compressionType) {
//		switch (compressionType) {
//			case NONE:
//				
//				break;
//			case GZIP:
//				this(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE)));
//				break;
//			case ZLIB:
//				break;
//		}
//		super(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE)));
//	}
	
	public void close() {
		try {
			((GZIPOutputStream)(out)).finish();
			this.flush();
			super.close();
		} catch (Exception e) {Logging.logger.error(e.getMessage(), e);}
	}
	
}
