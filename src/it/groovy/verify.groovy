/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
import com.jcabi.xml.XMLDocument

def log = new File(basedir, 'build.log')
def fist = new XMLDocument(new File(basedir, 'first.xmir'))
def same = new XMLDocument(new File(basedir, 'same.xmir'))

assert fist.equals(same)

assert log.text.contains("BUILD SUCCESS")
