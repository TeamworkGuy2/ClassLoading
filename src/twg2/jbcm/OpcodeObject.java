package twg2.jbcm;

public class OpcodeObject implements Opcode {
	private final int opcode;
	private OpcodeOperations operations;


	public OpcodeObject(int opcode, OpcodeOperations operations) {
		this.opcode = opcode;
		this.operations = operations;
	}


	public OpcodeOperations getOperations() {
		return operations;
	}


	@Override
	public int opcode() {
		return opcode;
	}

}
