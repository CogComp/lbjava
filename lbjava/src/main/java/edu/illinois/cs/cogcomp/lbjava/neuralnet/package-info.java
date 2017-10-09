/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
/**
 *  This package contains the Neural Network implemented employed by LBJava. This
 * implementation supports bias, momentum and back prop, and is designed with
 * efficiency in mind. The implementation contract includes an API for trainers
 * {@see NNTrainingInterface} that defines the API for the any trainers. A single
 * threaded trainer is provided. There is also a multithreaded trainer, which helps
 * when there are a very large number of weights between layers.<p>
 * 
 * There is also a {@see Layer} class which implements functionality specific 
 * to neural net layers within the system. However, there is no representation of
 * neuron within the API, this was decided upon to ensure good performance.
 * @author redman
  */
package edu.illinois.cs.cogcomp.lbjava.neuralnet;
