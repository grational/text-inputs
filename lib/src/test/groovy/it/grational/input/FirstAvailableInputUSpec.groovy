package it.grational.input

import spock.lang.*

class FirstAvailableInputUSpec extends Specification {

	def "Should return the first available among the inputs passed"() {
		given:
			def first  = Mock(TextInput)
			first.available() >> firstAvailable
		and:
			def second = Mock(TextInput)
			second.available() >> secondAvailable
		when:
			new FirstAvailableInput (
				first,
				second
			).text
		then:
			expectedFirst  * first.getText()
			expectedSecond * second.getText()
		where:
			firstAvailable | secondAvailable || expectedFirst | expectedSecond
			true           | false           || 1             | 0
			false          | true            || 0             | 1
			true           | true            || 1             | 0
	}

	def "Should raise an exception if no input is available"() {
		given:
			def first = Stub(TextInput)
			first.available() >> false
		and:
			def second = Stub(TextInput)
			second.available() >> false
		when:
			new FirstAvailableInput (
				first,
				second
			).text
		then:
			def exception = thrown(IllegalStateException)
			exception.message == "[FirstAvailableInput] No input available!"
	}

}
