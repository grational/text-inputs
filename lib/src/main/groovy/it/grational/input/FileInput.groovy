package it.grational.input

import groovy.transform.Canonical

@Canonical
class FileInput implements TextInput {
	File input

	@Override
	Boolean available() {
		this.input.canRead()
	}

	@Override
	String getText() {
		this.input.getText()
	}

}
