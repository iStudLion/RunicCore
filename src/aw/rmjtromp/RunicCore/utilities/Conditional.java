package aw.rmjtromp.RunicCore.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Conditional {
	
	private String condition;
	private final UUID identifier = UUID.randomUUID();
	
	private Conditional(String condition) {
		this.condition = condition;
	}

	public static Conditional parse(String condition) {
		return new Conditional(condition);
	}
	
	private boolean containsMultipleCondition(String condition) {
		if(condition.contains("||") || condition.contains("&&")) return true;
		return false;
	}
	
	private boolean containsComparison(String condition) {
		if(condition.contains("==")
				|| condition.contains("!=")
				|| condition.contains("<=")
				|| condition.contains(">=")
				|| condition.contains("<")
				|| condition.contains(">")
				) return true;
		return false;
	}
	
	public boolean getResults() {
		/*
		 * If the condition has a multiple condition inside of it, separate them and let the new conditionals solve it
		 * if it doesn't have any other conditions inside of it, just solve it
		 */
		String condition = this.condition;
		
		while(containsPriorities(condition)) {
			Pattern pattern = Pattern.compile("\\(([^\\)\\(]{1,})\\)");
			Matcher matcher = pattern.matcher(condition);
			
			while (matcher.find()) {
				if(containsMathEquation(matcher.group(1))) {
					condition = condition.replace(matcher.group(0), solveEquation(matcher.group(1))+"");
				} else condition = condition.replace(matcher.group(0), Conditional.parse(matcher.group(1)).getResults() ? "true" : "false");
			}
		}
		
		if(containsMultipleCondition(condition)) {
			if(condition.contains("||")) {
				String[] conditions = condition.split("(?:\\s*)?\\|\\|(?:\\s*)?");
				for(int i = 0; i < conditions.length; i++) {
					if(containsMultipleCondition(conditions[i]) || containsComparison(conditions[i])) {
						conditions[i] = Conditional.parse(conditions[i]).getResults() ? "true" : "false";
					}
				}
				condition = String.join("||", conditions);
			}
			
			if(condition.contains("&&")) {
				String[] conditions = condition.split("(?:\\s*)?&&(?:\\s*)?");
				for(int i = 0; i < conditions.length; i++) {
					if(containsMultipleCondition(conditions[i]) && containsComparison(conditions[i])) {
						conditions[i] = Conditional.parse(conditions[i]).getResults() ? "true" : "false";
					}
				}
				condition = String.join("&&", conditions);
			}
		}
		
		while(condition.contains("&&")) {
			condition = condition.replaceAll("(?:\\s*)?true(?:\\s*)?&&(?:\\s*)?true(?:\\s*)?", "true");
			condition = condition.replaceAll("(?:\\s*)?(?:true|false)(?:\\s*)?&&(?:\\s*)?false(?:\\s*)?", "false");
			condition = condition.replaceAll("(?:\\s*)?false(?:\\s*)?&&(?:\\s*)?(?:true|false)(?:\\s*)?", "false");
		}
		
		if(condition.contains("||")) {
			String[] conditions = condition.split("(?:\\s*)?\\|\\|(?:\\s*)?");
			for(String cond : conditions) {
				if(containsComparison(cond)) cond = solveComparison(cond) ? "true" : "false";
				if(solve(cond)) return true;
			}
		} else {
			if(containsComparison(condition)) condition = solveComparison(condition) ? "true" : "false";
			if(solve(condition)) return true;
		}
		
		return false;
	}
	
	private boolean containsPriorities(String cond) {
		return Pattern.compile("\\(([^\\)\\(]{1,})\\)").matcher(cond).find();
	}
	
	private boolean solveComparison(String comparison) {
		if(containsComparison(comparison)) {
			String comparisonType = "==";
			if(comparison.contains("==")) comparisonType = "==";
			else if(comparison.contains("!=")) comparisonType = "!=";
			else if(comparison.contains("<=")) comparisonType = "<=";
			else if(comparison.contains(">=")) comparisonType = ">=";
			else if(comparison.contains("<")) comparisonType = "<";
			else if(comparison.contains(">")) comparisonType = ">";
			
			String Left = comparison.split("(?:\\s*)?"+comparisonType+"(?:\\s*)?", 2)[0];
			String Right = comparison.split("(?:\\s*)?"+comparisonType+"(?:\\s*)?", 2)[1];
			
			char leftQuote = Left.charAt(0) == '`' ? '`' : Left.charAt(0) == '\'' ? '\'' : '"';
			char rightQuote = Right.charAt(0) == '`' ? '`' : Right.charAt(0) == '\'' ? '\'' : '"';
			
			Object LHC = null;
			Object RHC = null;

			if(Left.matches("^"+leftQuote+"(.{1,}?)"+leftQuote+"$")) LHC = new String(Left.substring(1, Left.length()-1));
			else if(Left.equalsIgnoreCase("false") || Left.equalsIgnoreCase("!true")) LHC = false;
			else if(Left.equalsIgnoreCase("true") || Left.equalsIgnoreCase("!false")) LHC = true;
			else if(Left.matches("^([-+]?(?:\\d+(?:\\.\\d+)?|(?:\\.\\d+)))$")) {
				if(Left.charAt(0) == '+') Left = Left.substring(1);
				
				// if it contains a dot (indicates that it has decimals)
				if(Left.contains(".")) {
					try { LHC = Double.parseDouble(Left); } catch (Exception no) {}
				} else {
					try { LHC = Integer.parseInt(Left); } catch (Exception no) {}
				}
			} else if(containsMathEquation(Left)) {
				LHC = solveEquation(Left);
			} else LHC = null;
			
			if(Right.matches("^"+rightQuote+"(.*?)"+rightQuote+"$")) RHC = new String(Right.substring(1, Right.length()-1));
			else if(Right.equalsIgnoreCase("false") || Right.equalsIgnoreCase("!true")) RHC = false;
			else if(Right.equalsIgnoreCase("true") || Right.equalsIgnoreCase("!false")) RHC = true;
			else if(Right.matches("^([-+]?(?:\\d+(?:\\.\\d+)?|(?:\\.\\d+)))$")) {
				if(Right.charAt(0) == '+') Right = Right.substring(1);
				
				// if it contains a dot (indicates that it has decimals)
				if(Right.contains(".")) {
					try { RHC = Double.parseDouble(Right); } catch (Exception no) {}
				} else {
					try { RHC = Integer.parseInt(Right); } catch (Exception no) {}
				}
			} else if(containsMathEquation(Right)) {
				RHC = solveEquation(Right);
			} else RHC = null;
			
			
			if(RHC == null || LHC == null) return false;
			else if(comparisonType.equals("==")) {
				return LHC.equals(RHC);
			} else if(comparisonType.equals("!=")) {
				return !LHC.equals(RHC);
			} else if(comparisonType.equals("<=") || comparisonType.equals(">=") || comparisonType.equals("<") || comparisonType.equals(">")) {
				if((LHC instanceof Integer || LHC instanceof Double) && (RHC instanceof Integer || RHC instanceof Double)) {
					if(LHC instanceof Integer) {
						if(RHC instanceof Integer) {
							if(comparisonType.equals("<=")) return (Integer) LHC <= (Integer) RHC;
							else if(comparisonType.equals(">=")) return (Integer) LHC >= (Integer) RHC;
							else if(comparisonType.equals("<")) return (Integer) LHC < (Integer) RHC;
							else if(comparisonType.equals(">")) return (Integer) LHC > (Integer) RHC;
						} else if(RHC instanceof Double) {
							if(comparisonType.equals("<=")) return (Integer) LHC <= (Double) RHC;
							else if(comparisonType.equals(">=")) return (Integer) LHC >= (Double) RHC;
							else if(comparisonType.equals("<")) return (Integer) LHC < (Double) RHC;
							else if(comparisonType.equals(">")) return (Integer) LHC > (Double) RHC;
						}
					} else {
						if(RHC instanceof Integer) {
							if(comparisonType.equals("<=")) return (Double) LHC <= (Integer) RHC;
							else if(comparisonType.equals(">=")) return (Double) LHC >= (Integer) RHC;
							else if(comparisonType.equals("<")) return (Double) LHC < (Integer) RHC;
							else if(comparisonType.equals(">")) return (Double) LHC > (Integer) RHC;
						} else if(RHC instanceof Double) {
							if(comparisonType.equals("<=")) return (Double) LHC <= (Double) RHC;
							else if(comparisonType.equals(">=")) return (Double) LHC >= (Double) RHC;
							else if(comparisonType.equals("<")) return (Double) LHC < (Double) RHC;
							else if(comparisonType.equals(">")) return (Double) LHC > (Double) RHC;
						}
					}
				}
			}
		}
		return false;
	}
	
	private boolean containsMathEquation(String LHC) {
		if(LHC.matches("(?:\\s*)?(?:[-+]?(?:\\d+(?:\\.\\d+)?|(?:\\.\\d+)))(?:\\s*)?(?:(?:[+-\\/*])(?:\\s*)?(?:[-+]?(?:\\d+(?:\\.\\d+)?|(?:\\.\\d+)))(?:\\s*)?){1,}")) return true;
		return false;
	}
	
	private Integer solveEquation(String equation) {
		List<String> a = Arrays.asList(equation.replaceAll("(?:\\s*)?", "").split("(?<=[*\\/+-])|(?=[*\\/+-])"));
		List<String> b = new ArrayList<String>();
		for(int i = 0; i < a.size(); i++) {
			if(a.get(i).matches("^[+-]$") && a.get(i-1).matches("^[*\\/+-]$")) {
				a.set(i+1, a.get(i)+a.get(i+1));
				continue;
			}
			b.add(a.get(i));
		}
		
		while(b.contains("*") || b.contains("/")) {
			// use break; to restart the loop
			for(int i = 0; i < b.size(); i++) {
				if(b.get(i).equals("*") || b.get(i).equals("/")) {
					if(b.get(i - 1) == null) {
						b.remove(i); break;
					} else if(b.get(i + 1) == null) {
						b.remove(i); break;
					}

					try {
						int lefthandnum = Integer.parseInt(b.get(i-1));
						int righthandnum = Integer.parseInt(b.get(i+1));
						
						if(b.get(i).equals("*")) {
							b.set(i-1, lefthandnum * righthandnum + "");
							b.remove(i);
							b.remove(i);
							break;
						} else {
							b.set(i-1, lefthandnum / righthandnum + "");
							b.remove(i);
							b.remove(i);
							break;
						}
					} catch(Exception no) {
						no.printStackTrace();
						return 0;
					}
				}
			}
		}
		
		while(b.contains("+") || b.contains("-")) {
			for(int i = 0; i < b.size(); i++) {
				if(b.get(i).equals("+") || b.get(i).equals("-")) {
					if(b.get(i - 1) == null) {
						b.remove(i); break;
					} else if(b.get(i + 1) == null) {
						b.remove(i); break;
					}

					try {
						int lefthandnum = Integer.parseInt(b.get(i-1));
						int righthandnum = Integer.parseInt(b.get(i+1));
						
						if(b.get(i).equals("+")) {
							b.set(i-1, lefthandnum + righthandnum + "");
							b.remove(i);
							b.remove(i);
							break;
						} else {
							b.set(i-1, lefthandnum - righthandnum + "");
							b.remove(i);
							b.remove(i);
							break;
						}
					} catch(Exception no) {
						return 0;
					}					
				}
			}
		}
		
		try {
			if(b.size() == 1) {
				return Integer.parseInt(b.get(0));
			}
		} catch(Exception no) {}
		return 0;
	}
	
	/**
	 * this is used after a comparison is solved
	 * for example 5 == 5, replace it with "true"
	 * and this parse it as boolean
	 * @param conditional
	 * @return Boolean
	 */
	private boolean solve(String condition) {
		// checks if its empty
		if(condition.isEmpty()) return false;
		// checks if its a number
		else if(condition.matches("^([-+]?(?:[0-9]+(?:\\.[0-9]+)?|(?:\\.[0-9]+)))$")) {
			// removes + sign from string if it has it
			if(condition.charAt(0) == '+') condition = condition.substring(1);
			
			// if it contains a dot (indicates that it has decimals)
			if(condition.contains(".")) {
				try {
					Double number = Double.parseDouble(condition);
					if(number > 0 || number < 0) return true;
					else return false;
				} catch (Exception no) {
					return false;
				}
			} else {
				try {
					Integer number = Integer.parseInt(condition);
					if(number > 0 || number < 0) return true;
					else return false;
				} catch (Exception no) {
					return false;
				}
			}
		} else if(condition.equalsIgnoreCase("true") || condition.equalsIgnoreCase("!false")) return true;
		else if(condition.equalsIgnoreCase("null") || condition.equalsIgnoreCase("false") || condition.equalsIgnoreCase("!true")) return false;
		else {
			// string always return true
			char leftQuote = condition.charAt(0) == '`' ? '`' : condition.charAt(0) == '\'' ? '\'' : '"';
			if(condition.matches("^"+leftQuote+"(.{1,}?)"+leftQuote+"$")) return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Conditional) {
			if(identifier.equals(((Conditional) obj).identifier) || condition == ((Conditional) obj).condition) return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return condition;
	}
	
}
