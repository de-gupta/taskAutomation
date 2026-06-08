package de.gupta.automation.task.printtext.adapter.rest;

record PrintTextRequest(String text, int repeatCount, String prefix, boolean upperCase)
{
}
