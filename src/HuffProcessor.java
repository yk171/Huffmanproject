
/**
 * Although this class has a history of several years,
 * it is starting from a blank-slate, new and clean implementation
 * as of Fall 2018.
 * <P>
 * Changes include relying solely on a tree for header information
 * and including debug and bits read/written information
 * 
 * @author Owen Astrachan
 */

public class HuffProcessor {

	public static final int BITS_PER_WORD = 8;
	public static final int BITS_PER_INT = 32;
	public static final int ALPH_SIZE = (1 << BITS_PER_WORD); 
	public static final int PSEUDO_EOF = ALPH_SIZE;
	public static final int HUFF_NUMBER = 0xface8200;
	public static final int HUFF_TREE  = HUFF_NUMBER | 1;

	private final int myDebugLevel;
	
	public static final int DEBUG_HIGH = 4;
	public static final int DEBUG_LOW = 1;
	
	public HuffProcessor() {
		this(0);
	}
	
	public HuffProcessor(int debug) {
		myDebugLevel = debug;
	}

	/**
	 * Compresses a file. Process must be reversible and loss-less.
	 *
	 * @param in
	 *            Buffered bit stream of the file to be compressed.
	 * @param out
	 *            Buffered bit stream writing to the output file.
	 */
	public void compress(BitInputStream in, BitOutputStream out){
		int[] counts = readForCounts(in);
		HuffNode root = makeTreeFromCounts(counts);
		String[] codings = makeCodingsFromTree(root);
		
		out.writeBits(BITS_PER_INT, HUFF_TREE);
		writeHeader(root,out);
		
		in.reset();
		writeCompressedBits(codings, in, out);
		out.close();
	}


	private int[] readForCounts(BitInputStream in) {
		int[] freq = new int[ALPH_SIZE + 1];
		while(in.readBits(BITS_PER_WORD) != -1) {
			int bit = in.readBits(BITS_PER_WORD);
			freq[PSEUDO_EOF] = 1;
			if(bit == PSEUDO_EOF) freq[PSEUDO_EOF] = 1;
			else freq[bit] +=1;
		}
		
		//in.readBits(in);
		return freq;
	}

	private HuffNode makeTreeFromCounts(int[] counts) {
		// TODO Auto-generated method stub
		return null;
	}

	private String[] makeCodingsFromTree(HuffNode root) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void writeHeader(HuffNode root, BitOutputStream out) {
		// TODO Auto-generated method stub
		
	}
	
	private void writeCompressedBits(String[] codings, BitInputStream in, BitOutputStream out) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Decompresses a file. Output file must be identical bit-by-bit to the
	 * original.
	 *
	 * @param in
	 *            Buffered bit stream of the file to be decompressed.
	 * @param out
	 *            Buffered bit stream writing to the output file.
	 */
	public void decompress(BitInputStream in, BitOutputStream out){
		int bits = in.readBits(BITS_PER_INT);
		if(bits != HUFF_TREE)
			throw new HuffException("illegal header starts with" + bits);
		HuffNode root = readTreeHeader(in);
		readCompressedBits(root,in,out);
//		while (true){
//			int val = in.readBits(BITS_PER_WORD);
//			if (val == -1) break;
//			out.writeBits(BITS_PER_WORD, val);
//		}
		out.close();	
	}
	
	private HuffNode readTreeHeader(BitInputStream in) {
		int bit = in.readBits(1);
		if(bit == -1) throw new HuffException("illegal header starts with" + bit);
		if(bit == 0) {
			HuffNode left = readTreeHeader(in);
			HuffNode right = readTreeHeader(in);
			return new HuffNode(0, 0, left, right);
			}
			else {
				int value = in.readBits(9);
				return new HuffNode(value, 0, null, null);
			}
	}

	private void readCompressedBits(HuffNode root, BitInputStream in, BitOutputStream out) {
		 HuffNode current = root; 
		   while (true) {
		       int bits = in.readBits(1);
		       if (bits == -1) {
		           throw new HuffException("bad input, no PSEUDO_EOF");
		       }
		       else { 
		           if (bits == 0) current = current.myLeft;
		      else current = current.myRight;

		           if (current.myRight == null && current.myLeft == null) {
		               if (current.myValue == PSEUDO_EOF) 
		                   break;   // out of loop
		               else {
		                   out.writeBits(BITS_PER_WORD, current.myValue);
		                   current = root; // start back after leaf
		               }
		           }
		       }
		   }
		
	}

	
}