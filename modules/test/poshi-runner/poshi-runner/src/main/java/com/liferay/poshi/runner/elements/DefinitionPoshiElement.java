/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.poshi.runner.elements;

import com.liferay.poshi.runner.script.PoshiScriptParserException;
import com.liferay.poshi.runner.util.Dom4JUtil;
import com.liferay.poshi.runner.util.FileUtil;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * @author Kenji Heigel
 */
public class DefinitionPoshiElement extends PoshiElement {

	@Override
	public PoshiElement clone(Element element) {
		return clone(element, null);
	}

	public PoshiElement clone(Element element, URL url) {
		if (isElementType(getElementName(), element)) {
			return new DefinitionPoshiElement(element, url);
		}

		return null;
	}

	@Override
	public PoshiElement clone(
			PoshiElement parentPoshiElement, String poshiScript)
		throws PoshiScriptParserException {

		return clone(poshiScript, null);
	}

	public PoshiElement clone(String poshiScript, URL url)
		throws PoshiScriptParserException {

		if (isElementType(poshiScript)) {
			return new DefinitionPoshiElement(poshiScript, url);
		}

		return null;
	}

	public String getFileExtension() {
		String filePath = getFilePath();

		int index = filePath.lastIndexOf(".");

		return filePath.substring(index + 1);
	}

	public String getFilePath() {
		return _filePath;
	}

	@Override
	public int getPoshiScriptLineNumber() {
		return getPoshiScriptLineNumber(false);
	}

	@Override
	public boolean isValidPoshiXML() {
		if (_validPoshiXML == null) {
			try {
				_validPoshiXML = false;

				PoshiNodeFactory.setValidatePoshiScript(false);

				URL url = FileUtil.getURL(new File(getFilePath()));

				PoshiNode poshiNode = PoshiNodeFactory.newPoshiNodeFromFile(
					url);

				String poshiScript = poshiNode.toPoshiScript();

				PoshiNode generatedPoshiNode = PoshiNodeFactory.newPoshiNode(
					poshiScript, url);

				if (Dom4JUtil.elementsEqual(poshiNode, generatedPoshiNode)) {
					_validPoshiXML = true;
				}

				PoshiNodeFactory.setValidatePoshiScript(true);
			}
			catch (MalformedURLException murle) {
			}
		}

		return _validPoshiXML;
	}

	@Override
	public void parsePoshiScript(String poshiScript)
		throws PoshiScriptParserException {

		Matcher poshiScriptAnnotationMatcher =
			poshiScriptAnnotationPattern.matcher(getBlockName(poshiScript));

		while (poshiScriptAnnotationMatcher.find()) {
			String annotation = poshiScriptAnnotationMatcher.group();

			String name = getNameFromAssignment(annotation);
			String value = getDoubleQuotedContent(annotation);

			addAttribute(name, value);
		}

		String blockContent = getBlockContent(poshiScript);

		for (String poshiScriptSnippet : getPoshiScriptSnippets(blockContent)) {
			add(PoshiNodeFactory.newPoshiNode(this, poshiScriptSnippet));
		}
	}

	@Override
	public String toPoshiScript() {
		StringBuilder sb = new StringBuilder();

		for (PoshiElementAttribute poshiElementAttribute :
				toPoshiElementAttributes(attributeList())) {

			sb.append("\n@");

			sb.append(poshiElementAttribute.toPoshiScript());
		}

		sb.append(createPoshiScriptBlock(getPoshiNodes()));

		String string = sb.toString();

		return string.trim();
	}

	protected DefinitionPoshiElement() {
	}

	protected DefinitionPoshiElement(Element element, URL url) {
		super(_ELEMENT_NAME, element, url);
	}

	protected DefinitionPoshiElement(
		List<Attribute> attributes, List<Node> nodes) {

		super(_ELEMENT_NAME, attributes, nodes);
	}

	protected DefinitionPoshiElement(
			PoshiElement parentPoshiElement, String poshiScript)
		throws PoshiScriptParserException {

		super(_ELEMENT_NAME, parentPoshiElement, poshiScript);
	}

	protected DefinitionPoshiElement(String poshiScript, URL url)
		throws PoshiScriptParserException {

		super(_ELEMENT_NAME, null, poshiScript, url);
	}

	@Override
	protected String getBlockName() {
		return "definition";
	}

	protected String getElementName() {
		return _ELEMENT_NAME;
	}

	@Override
	protected String getPad() {
		return "";
	}

	protected String getPoshiScriptKeyword() {
		String fileExtension = getFileExtension();

		if (fileExtension.equals("testcase")) {
			return "test";
		}

		return fileExtension;
	}

	protected boolean isElementType(String poshiScript) {
		return isValidPoshiScriptBlock(_blockNamePattern, poshiScript);
	}

	protected void setFilePath(URL url) {
		_filePath = url.getFile();
	}

	private static final String _ELEMENT_NAME = "definition";

	private static final String _POSHI_SCRIPT_KEYWORD = _ELEMENT_NAME;

	private static final Pattern _blockNamePattern = Pattern.compile(
		"^" + BLOCK_NAME_ANNOTATION_REGEX + _POSHI_SCRIPT_KEYWORD,
		Pattern.DOTALL);

	private String _filePath;
	private Boolean _validPoshiXML;

}