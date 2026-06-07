package de.gupta.automation.task.adapter;

public record FacadeInputShape<I, O, MO, OO>(I input, O output, MO mandatoryOptions, OO optionalOptions)
{
}