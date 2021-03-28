package it.grational.input

class FirstAvailableInput implements TextInput {
	TextInput[] inputs

	FirstAvailableInput(TextInput... inputs) {
		this.inputs = inputs
	}

	@Override
	Boolean available() {
		this.firstAvailable()
	}

	@Override
	String getText() {
		this.firstAvailable().getText()
	}

	private TextInput firstAvailable() {
		this.inputs.find { it.available() } ?: { throw new IllegalStateException("[${this.class.simpleName}] No input available!") }()
	}
}
