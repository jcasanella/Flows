package com.bin.data.flows.exceptions

final case class ConfigParamsException(private val message: String = "",
                                        private val cause: Throwable = None.orNull) extends Exception(message, cause)
