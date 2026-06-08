package de.gupta.automation.task.implementations.text.print.adapter.rest;

record PrintTextRequest(String text, int repeatCount, String prefix, boolean upperCase)
{
}