package model.change.rules;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import errors.BooleanWrapper;

import model.change.ChangeEvent;
import model.change.ChangeRelated;
import model.formaldef.Describable;
import model.formaldef.FormalDefinition;
import model.formaldef.components.FormalDefinitionComponent;
import model.formaldef.components.alphabets.Alphabet;
import model.formaldef.components.symbols.Symbol;



/**
 * A basic formal definition rule. These are used to check if a ChangeEvent
 * should be applied in a given situation. Typically, if the rule inhibits 
 * the application of the event, then the event should NOT be applied.
 * @author Julian
 *
 */
public abstract class FormalDefinitionRule extends Rule{

	





}