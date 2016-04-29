// Copyright (c) 2011 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.


function onClick() {
  chrome.tabs.executeScript(null, { code: "test()" });
}

chrome.browserAction.onClicked.addListener(onClick);
