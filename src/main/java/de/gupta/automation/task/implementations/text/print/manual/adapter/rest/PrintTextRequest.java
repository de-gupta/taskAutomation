package de.gupta.automation.task.implementations.text.print.manual.adapter.rest;

record PrintTextRequest(String text, int repeatCount, String prefix, boolean upperCase)
{
}