package org.sid.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Question {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
		@Column(name="questionText")
	  private String questionText;
		@Column(name="option_a")
	    private String optionA;
		@Column(name = "option_b")
	    private String optionB;
		@Column(name="option_c")
	    private String optionC;
		@Column(name="correctAnswer")
	    private String correctAnswer;
		
		public Question() {
			
		}
		public Question(String questionText, String optionA, String optionB, String optionC, String correctAnswer) {
			super();
			this.questionText = questionText;
			this.optionA = optionA;
			this.optionB = optionB;
			this.optionC = optionC;
			this.correctAnswer = correctAnswer;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getQuestionText() {
			return questionText;
		}
		public void setQuestionText(String questionText) {
			this.questionText = questionText;
		}
		public String getOptionA() {
			return optionA;
		}
		public void setOptionA(String optionA) {
			this.optionA = optionA;
		}
		public String getOptionB() {
			return optionB;
		}
		public void setOptionB(String optionB) {
			this.optionB = optionB;
		}
		public String getOptionC() {
			return optionC;
		}
		public void setOptionC(String optionC) {
			this.optionC = optionC;
		}
		public String getCorrectAnswer() {
			return correctAnswer;
		}
		public void setCorrectAnswer(String correctAnswer) {
			this.correctAnswer = correctAnswer;
		}
		
	
	
}