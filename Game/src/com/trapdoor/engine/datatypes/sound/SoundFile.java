package com.trapdoor.engine.datatypes.sound;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL11;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.file.Files;

/**
 * @author laptop
 * @date Feb. 10, 2022
 * 
 */
public class SoundFile {

	private final int bufferId;

	private ShortBuffer pcm = null;

	private ByteBuffer vorbis = null;
	private STBVorbisInfo info;

	public SoundFile(String file) throws Exception {
		this.bufferId = AL11.alGenBuffers();
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			readVorbis(file, 32 * 1024, info);
			this.info = info;
			// Copy to buffer
			
		}
	}
	
	public void loadAudioBuffer() {
		AL11.alBufferData(bufferId, info.channels() == 1 ? AL11.AL_FORMAT_MONO16 : AL11.AL_FORMAT_STEREO16, pcm, info.sample_rate());
	}

	public int getBufferId() {
		return this.bufferId;
	}

	public void cleanup() {
		AL11.alDeleteBuffers(this.bufferId);
	}

	private ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) throws Exception {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			vorbis = ioResourceToByteBuffer(resource, bufferSize);
			IntBuffer error = stack.mallocInt(1);
			long decoder = STBVorbis.stb_vorbis_open_memory(vorbis, error, null);
			if (decoder == MemoryUtil.NULL) {
				throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
			}

			STBVorbis.stb_vorbis_get_info(decoder, info);

			int channels = info.channels();

			int lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);

			pcm = MemoryUtil.memAllocShort(lengthSamples);

			pcm.limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
			STBVorbis.stb_vorbis_close(decoder);

			return pcm;
		}
	}

	private static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer buffer;

		Path path = Paths.get(resource);
		if (Files.isReadable(path)) {
			try (SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
				while (fc.read(buffer) != -1)
					;
			}
		} else {
			try (InputStream source = SoundFile.class.getResourceAsStream(resource);
					ReadableByteChannel rbc = Channels.newChannel(source)) {
				buffer = BufferUtils.createByteBuffer(bufferSize);

				while (true) {
					int bytes = rbc.read(buffer);
					if (bytes == -1) {
						break;
					}
					if (buffer.remaining() == 0) {
						buffer = resizeBuffer(buffer, buffer.capacity() * 2);
					}
				}
			}
		}

		buffer.flip();
		return buffer;
	}

	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}
}
