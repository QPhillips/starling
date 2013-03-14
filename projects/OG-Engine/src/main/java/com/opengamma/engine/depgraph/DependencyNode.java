/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.depgraph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.opengamma.core.security.Security;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.MemoryUtils;
import com.opengamma.engine.function.CompiledFunctionDefinition;
import com.opengamma.engine.function.MarketDataSourcingFunction;
import com.opengamma.engine.function.ParameterizedFunction;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.PublicAPI;

/**
 * A single node in a {@link DependencyGraph}. A node represents the need to execute a particular function at
 * runtime to produce certain outputs.
 * <p>
 * The same node instance can belong to multiple graphs due to the possibility of sub-graphing.
 * <p>
 * A node consists of a computation target (e.g. a {@link Security}), a function to operate on that target, input
 * values for the function, and output values generated by the function. Relationships with other nodes - either input
 * or dependent - indicate how input values are produced and how output values are to be used.
 */
@PublicAPI
public class DependencyNode {

  // BELOW: COMPLETELY IMMUTABLE VARIABLES

  private final ComputationTargetSpecification _computationTarget;

  // COMPLETELY IMMUTABLE VARIABLES END

  private ParameterizedFunction _function;

  // BELOW: EVEN THOUGH VARIABLE ITSELF IS FINAL, CONTENTS ARE MUTABLE.

  private final Set<ValueSpecification> _inputValues = new HashSet<ValueSpecification>();
  private final Set<ValueSpecification> _outputValues = new HashSet<ValueSpecification>();

  private final Set<DependencyNode> _inputNodes = new HashSet<DependencyNode>();
  private final Set<DependencyNode> _dependentNodes = new HashSet<DependencyNode>();

  /**
   * The final output values that cannot be stripped from the {@link #_outputValues} set no matter
   * whether there are no dependent nodes.
   */
  private final Set<ValueSpecification> _terminalOutputValues = new HashSet<ValueSpecification>();

  // MUTABLE CONTENTS VARIABLES END

  /**
   * Creates a new node.
   *
   * @param target the computation target, not null
   */
  public DependencyNode(final ComputationTarget target) {
    this(MemoryUtils.instance(target.toSpecification()));
  }

  /**
   * Creates a new node.
   *
   * @param target the computation target specification, not null
   */
  public DependencyNode(final ComputationTargetSpecification target) {
    ArgumentChecker.notNull(target, "Computation Target");
    _computationTarget = target;
  }

  /**
   * Adds a set of nodes as inputs to this node. The nodes added are updated to include this node in their
   * dependent node set.
   *
   * @param inputNodes nodes to add, not null and not containing null
   */
  public void addInputNodes(final Set<DependencyNode> inputNodes) {
    for (final DependencyNode inputNode : inputNodes) {
      addInputNode(inputNode);
    }
  }

  /**
   * Adds a node as input to this node. The node added is updated to include this node in its dependent node
   * set.
   *
   * @param inputNode node to add, not null
   */
  public void addInputNode(final DependencyNode inputNode) {
    ArgumentChecker.notNull(inputNode, "Input Node");
    _inputNodes.add(inputNode);
    inputNode.addDependentNode(this);
  }

  protected void addDependentNode(final DependencyNode dependentNode) {
    ArgumentChecker.notNull(dependentNode, "Dependent Node");
    _dependentNodes.add(dependentNode);
  }

  /**
   * Returns the set of all immediately dependent nodes - i.e. nodes that consume one or more output values generated
   * by this node.
   *
   * @return the set of dependent nodes
   */
  public Set<DependencyNode> getDependentNodes() {
    return Collections.unmodifiableSet(_dependentNodes);
  }

  /**
   * Returns the set of all immediate input nodes - i.e. nodes that produce one or more of the input values to the function
   * attached to this node.
   *
   * @return the set of input nodes
   */
  public Set<DependencyNode> getInputNodes() {
    return Collections.unmodifiableSet(_inputNodes);
  }

  /**
   * Adds output values to this node. Graph construction will initially include the maximal set of outputs from the function.
   * This will later be pruned to remove any values not required as inputs to other nodes and not specified as terminal outputs
   * of the graph.
   *
   * @param outputValues the output values produced by this node, not null
   */
  public void addOutputValues(final Set<ValueSpecification> outputValues) {
    for (final ValueSpecification outputValue : outputValues) {
      addOutputValue(outputValue);
    }
  }

  /**
   * Adds an output value to the node. Graph construction will initially include the maximal set of outputs from the function.
   * This will later be pruned to remove any values not required as inputs to other nodes and not specified as terminal outputs
   * of the graph.
   *
   * @param outputValue an output value produced by this node, not null
   */
  public void addOutputValue(final ValueSpecification outputValue) {
    ArgumentChecker.notNull(outputValue, "Output value");
    _outputValues.add(outputValue);
  }

  /**
   * Removes an output value from this node. The value must not be used as an input to a dependent node.
   *
   * @param outputValue the value to remove
   */
  public void removeOutputValue(final ValueSpecification outputValue) {
    for (final DependencyNode outputNode : _dependentNodes) {
      if (outputNode._inputValues.contains(outputValue)) {
        throw new IllegalStateException("Can't remove output value " + outputValue + " required for input to " + outputNode);
      }
    }
    if (!_outputValues.remove(outputValue)) {
      throw new IllegalStateException("Output value " + outputValue + " not in output set of " + this);
    }
  }

  /**
   * Replace an output value from this node with another. Returns the number of times the value is consumed by dependent nodes.
   *
   * @param existingOutputValue the existing value
   * @param newOutputValue the value to replace it with
   * @return the number of replacements made in dependenct nodes
   */
  public int replaceOutputValue(final ValueSpecification existingOutputValue, final ValueSpecification newOutputValue) {
    if (!_outputValues.remove(existingOutputValue)) {
      throw new IllegalStateException("Existing output value " + existingOutputValue + " not in output set of " + this);
    }
    _outputValues.add(newOutputValue);
    int count = 0;
    for (final DependencyNode outputNode : _dependentNodes) {
      if (outputNode._inputValues.remove(existingOutputValue)) {
        outputNode._inputValues.add(newOutputValue);
        count++;
      }
    }
    return count;
  }

  /* package */void clearInputs() {
    for (final DependencyNode inputNode : _inputNodes) {
      inputNode._dependentNodes.remove(this);
    }
    _inputNodes.clear();
    _inputValues.clear();
  }

  public void addInputValue(final ValueSpecification inputValue) {
    ArgumentChecker.notNull(inputValue, "Input value");
    _inputValues.add(inputValue);
  }

  /**
   * Replaces the dependency node that an input value is sourced from. If this was the only input value sourced from the previous input node then it is removed from the input node set.
   *
   * @param inputValue the input value to replace, not null
   * @param previousInputNode the node the data was being produced by, not null
   * @param newInputNode the new input node, not null
   */
  public void replaceInput(final ValueSpecification inputValue, final DependencyNode previousInputNode, final DependencyNode newInputNode) {
    addInputNode(newInputNode);
    for (final ValueSpecification input : _inputValues) {
      if (!inputValue.equals(input)) {
        if (previousInputNode._outputValues.contains(input)) {
          // Previous input still produces other values we consume
          return;
        }
      }
    }
    // Not consuming any other inputs from this node
    previousInputNode._dependentNodes.remove(this);
    _inputNodes.remove(previousInputNode);
  }

  /**
   * Cuts all edges linking this node, replacing it with the given node. The new node will be updated to include the input and output value specifications from this node.
   *
   * @param newNode the node to replace this in the graph
   */
  /* package */void replaceWith(final DependencyNode newNode) {
    for (final DependencyNode input : _inputNodes) {
      if (input._dependentNodes.remove(this)) {
        input._dependentNodes.add(newNode);
        newNode._inputNodes.add(input);
      }
    }
    newNode._inputValues.addAll(_inputValues);
    for (final DependencyNode output : _dependentNodes) {
      if (output._inputNodes.remove(this)) {
        output._inputNodes.add(newNode);
        newNode._dependentNodes.add(output);
      }
    }
    // Rewrite the original outputs to use the target of the new node
    for (final ValueSpecification outputValue : _outputValues) {
      final ValueSpecification newOutputValue = MemoryUtils.instance(new ValueSpecification(outputValue.getValueName(), newNode.getComputationTarget(), outputValue.getProperties()));
      newNode._outputValues.add(newOutputValue);
      for (final DependencyNode output : _dependentNodes) {
        if (output._inputValues.remove(outputValue)) {
          output._inputValues.add(newOutputValue);
        }
      }
    }
  }

  /**
   * Returns the set of output values produced by this node.
   *
   * @return the set of output values
   */
  public Set<ValueSpecification> getOutputValues() {
    return Collections.unmodifiableSet(_outputValues);
  }

  /* package */Set<ValueSpecification> getOutputValuesCopy() {
    return new HashSet<ValueSpecification>(_outputValues);
  }

  /**
   * Returns the set of terminal output values produced by this node. This is a subset of {@link #getOutputValues}. After
   * graph construction any output values that are not consumed by other nodes will be pruned unless they are declared
   * as terminal output values.
   *
   * @return the set of output values, or the empty set if none
   */
  public Set<ValueSpecification> getTerminalOutputValues() {
    return Collections.unmodifiableSet(_terminalOutputValues);
  }

  /**
   * Returns the set of input values.
   *
   * @return the set of input values
   */
  public Set<ValueSpecification> getInputValues() {
    return Collections.unmodifiableSet(_inputValues);
  }

  /* package */Set<ValueSpecification> getInputValuesCopy() {
    return new HashSet<ValueSpecification>(_inputValues);
  }

  /**
   * Tests if a given value is an input to this node.
   *
   * @param specification value to test
   * @return true if the value is an input to this node
   */
  public boolean hasInputValue(final ValueSpecification specification) {
    return _inputValues.contains(specification);
  }

  /**
   * Returns the market data requirement of this node.
   *
   * @return the market data requirement, or null if none
   */
  public ValueSpecification getRequiredMarketData() {
    if (_function.getFunction() instanceof MarketDataSourcingFunction) {
      return getOutputValues().iterator().next();
    }
    return null;
  }

  /**
   * Returns the function used at this node.
   *
   * @return the function
   */
  public ParameterizedFunction getFunction() {
    return _function;
  }

  /**
   * Uses default parameters to invoke the function. Useful in tests.
   *
   * @param function Function to be invoked
   */
  public void setFunction(final CompiledFunctionDefinition function) {
    setFunction(new ParameterizedFunction(function, function.getFunctionDefinition().getDefaultParameters()));
  }

  /**
   * Sets the function to be used to execute this node.
   *
   * @param function Function to be invoked
   */
  public void setFunction(final ParameterizedFunction function) {
    ArgumentChecker.notNull(function, "Function");
    if (_function != null) {
      throw new IllegalStateException("The function was already set");
    }
    // [PLAT-2286] We used to check the function's target was right for the target specification. This would require knowledge of
    // the resolution strategy to do properly. The function type has to be compatible with something that is a sub-type of the target.
    _function = function;
  }

  /**
   * Returns the computation target of the node.
   *
   * @return the computation target
   */
  public ComputationTargetSpecification getComputationTarget() {
    return _computationTarget;
  }

  /**
   * Removes any unused outputs. These are any output values that are not terminal output values and are not
   * stated as inputs to any dependent nodes.
   *
   * @return the set of outputs removed, or the empty set if none were removed
   */
  public Set<ValueSpecification> removeUnnecessaryOutputs() {
    final Set<ValueSpecification> unnecessaryOutputs = new HashSet<ValueSpecification>();
    for (final ValueSpecification outputSpec : _outputValues) {
      if (_terminalOutputValues.contains(outputSpec)) {
        continue;
      }
      boolean isUsed = false;
      for (final DependencyNode dependantNode : _dependentNodes) {
        if (dependantNode.hasInputValue(outputSpec)) {
          isUsed = true;
          break;
        }
      }
      if (!isUsed) {
        unnecessaryOutputs.add(outputSpec);
      }
    }
    _outputValues.removeAll(unnecessaryOutputs);
    return unnecessaryOutputs;
  }

  /**
   * Marks an output as terminal, meaning that it cannot be pruned. If this node already belongs to a graph, use
   * {@link DependencyGraph#addTerminalOutput(ValueRequirement requirement, ValueSpecification specification)}.
   *
   * @param terminalOutput  the output to mark as terminal
   */
  public void addTerminalOutputValue(final ValueSpecification terminalOutput) {
    _terminalOutputValues.add(terminalOutput);
  }

  /**
   * Unmarks an output as terminal, reversing {@link #addTerminalOutputValue}. The output will remain as an output of the node.
   *
   * @param terminalOutput the output to unmark as terminal
   */
  public void removeTerminalOutputValue(final ValueSpecification terminalOutput) {
    _terminalOutputValues.remove(terminalOutput);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("DependencyNode[");
    if (getFunction() != null) {
      sb.append(getFunction().getFunction().getFunctionDefinition().getShortName());
    } else {
      sb.append("<null function>");
    }
    sb.append(" on ");
    sb.append(getComputationTarget());
    sb.append("]");
    return sb.toString();
  }

}
