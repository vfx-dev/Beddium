/*
 * Beddium
 *
 * Copyright (C) 2025 Ven, FalsePattern
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.ventooth.beddium.modules.TerrainRendering;

import lombok.RequiredArgsConstructor;
import lombok.val;
import mega.trace.service.MEGATraceService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;

import java.util.List;

public final class Profiling {
    private static final String PROFILER_PREFIX = "bed_";
    private static final boolean MEGA_TRACE_ENABLED = MEGATraceService.INSTANCE.isEnabled();

    private static final ThreadLocal<Profiler> threadProfilers = ThreadLocal.withInitial(Profiling::makeThreadProfiler);

    private static @Nullable Thread mainThread;
    private static @Nullable Profiler mainProfiler;

    private Profiling() {
        throw new UnsupportedOperationException();
    }

    public static void markMainThread() {
        if (mainThread != null) {
            throw new IllegalStateException("Main thread already marked");
        }
        mainThread = Thread.currentThread();
    }

    public static Profiler getProfiler() {
        if (mainThread == null) {
            throw new IllegalStateException("Main Thread not marked");
        }
        if (Thread.currentThread() != mainThread) {
            return threadProfilers.get();
        }
        if (mainProfiler == null) {
            mainProfiler = new DelegatingProfiler(Minecraft.getMinecraft().mcProfiler);
        }
        return mainProfiler;
    }

    public static void addMessage(String fmt, Object... args) {
        if (MEGA_TRACE_ENABLED) {
            MEGATraceService.INSTANCE.message(fmt.formatted(args));
        }
    }

    private static @NotNull Profiler makeThreadProfiler() {
        if (MEGA_TRACE_ENABLED) {
            val profiler = new Profiler();
            MEGATraceService.INSTANCE.markProfiler(profiler, PROFILER_PREFIX, 0);
            return profiler;
        }
        return new DelegatingProfiler(null);
    }

    @RequiredArgsConstructor
    private static final class DelegatingProfiler extends Profiler {
        private final @Nullable Profiler delegate;

        @Override
        public void startSection(String name) {
            if (delegate != null) {
                delegate.startSection(PROFILER_PREFIX + name);
            }
        }

        @Override
        public void endStartSection(String name) {
            if (delegate != null) {
                delegate.endStartSection(PROFILER_PREFIX + name);
            }
        }

        @Override
        public void endSection() {
            if (delegate != null) {
                delegate.endSection();
            }
        }

        @Override
        public String getNameOfLastSection() {
            if (delegate != null) {
                return delegate.getNameOfLastSection();
            } else {
                return "[UNKNOWN]";
            }
        }

        @Override
        public void clearProfiling() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Result> getProfilingData(String profilerName) {
            throw new UnsupportedOperationException();
        }
    }
}
