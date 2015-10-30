package com.griddynamics.jagger.util;/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.google.common.base.Equivalence;

/**
 * Contains static factory methods for creating {@code Equivalence} instances.
 *
 * @author Mairbek Khadikov
 */
public abstract class Eq {
    private Eq() {

    }

    public static Equivalence<Object> alwaysTrue() {
        return AlwaysTrue.INSTANCE;
    }

    public static Equivalence<Object> alwaysFalse() {
        return AlwaysFalse.INSTANCE;
    }

    private static class AlwaysTrue extends Equivalence<Object> {
        private static AlwaysTrue INSTANCE = new AlwaysTrue();

        @Override
        protected boolean doEquivalent(Object a, Object b) {
            return true;
        }

        @Override
        protected int doHash(Object o) {
            return 0;
        }
    }

    private static class AlwaysFalse extends Equivalence<Object> {
        private static AlwaysFalse INSTANCE = new AlwaysFalse();

        @Override
        protected boolean doEquivalent(Object a, Object b) {
            return false;
        }

        @Override
        protected int doHash(Object o) {
            return 0;
        }
    }
}