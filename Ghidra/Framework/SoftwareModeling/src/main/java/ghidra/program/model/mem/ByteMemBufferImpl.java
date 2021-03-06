/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.program.model.mem;

import java.math.BigInteger;

import ghidra.program.model.address.Address;
import ghidra.util.GhidraBigEndianDataConverter;
import ghidra.util.GhidraLittleEndianDataConverter;

/**
 * Simple byte buffer implementation of the memBuffer.  Since there is no
 * actual memory object associated with this object, the getMemory method
 * is not implemented and all gets are limited to the bytes supplied during
 * construction.
 */
public class ByteMemBufferImpl implements MemBuffer {

	private byte[] bytes;
	private Address addr;
	private final boolean isBigEndian;

	/**
	 * Construct a ByteMemBufferImpl object
	 * @param addr the address to associate with the bytes
	 * @param bytes the data that normally would be coming from memory.
	 * @param isBigEndian true for BigEndian, false for LittleEndian.
	 */
	public ByteMemBufferImpl(Address addr, byte[] bytes, boolean isBigEndian) {
		this.addr = addr;
		this.bytes = bytes;
		this.isBigEndian = isBigEndian;
	}

	/**
	 * Convenience constructor using varargs for specifying byte values.
	 * @param addr the address to associate with the bytes
	 * @param isBigEndian true for BigEndian, false for LittleEndian.
	 * @param byteValues varargs for specifying the individual byte values.  The int argument
	 * will be truncated to a byte value.
	 */
	public ByteMemBufferImpl(Address addr, boolean isBigEndian, int... byteValues) {
		this.addr = addr;
		this.isBigEndian = isBigEndian;
		bytes = new byte[byteValues.length];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) byteValues[i];
		}
	}

	/**
	 * Get number of bytes contained within buffer
	 * @return byte count
	 */
	public int getLength() {
		return bytes.length;
	}

	@Override
	public Address getAddress() {
		return addr;
	}

	@Override
	public byte getByte(int offset) throws MemoryAccessException {
		if (offset < 0 || offset >= bytes.length) {
			throw new MemoryAccessException("Offset " + offset + " is not in range");
		}
		return bytes[offset];
	}

	@Override
	public Memory getMemory() {
		throw new UnsupportedOperationException("Can't get memory from ByteMemBuffer");
	}

	@Override
	public int getBytes(byte[] b, int offset) {
		if (offset < 0 || offset >= bytes.length) {
			return 0;
		}
		int len = Math.min(b.length, bytes.length - offset);
		System.arraycopy(bytes, offset, b, 0, len);
		return len;
	}

	@Override
	public boolean isBigEndian() {
		return isBigEndian;
	}

	@Override
	public short getShort(int offset) throws MemoryAccessException {
		if (isBigEndian) {
			return GhidraBigEndianDataConverter.INSTANCE.getShort(this, offset);
		}
		return GhidraLittleEndianDataConverter.INSTANCE.getShort(this, offset);
	}

	@Override
	public int getInt(int offset) throws MemoryAccessException {
		if (isBigEndian) {
			return GhidraBigEndianDataConverter.INSTANCE.getInt(this, offset);
		}
		return GhidraLittleEndianDataConverter.INSTANCE.getInt(this, offset);
	}

	@Override
	public long getLong(int offset) throws MemoryAccessException {
		if (isBigEndian) {
			return GhidraBigEndianDataConverter.INSTANCE.getLong(this, offset);
		}
		return GhidraLittleEndianDataConverter.INSTANCE.getLong(this, offset);
	}

	@Override
	public BigInteger getBigInteger(int offset, int size, boolean signed)
			throws MemoryAccessException {
		if (isBigEndian) {
			return GhidraBigEndianDataConverter.INSTANCE.getBigInteger(this, offset, size, signed);
		}
		return GhidraLittleEndianDataConverter.INSTANCE.getBigInteger(this, offset, size, signed);
	}
}
