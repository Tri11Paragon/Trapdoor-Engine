//
// Created by brett on 20/07/22.
//

#ifndef ENGINE_LOGGING_H
#define ENGINE_LOGGING_H

#include <boost/log/core.hpp>
#include <boost/log/trivial.hpp>
#include <boost/log/expressions.hpp>
#include <boost/log/utility/setup/file.hpp>
#include <boost/log/utility/setup/common_attributes.hpp>
#include <boost/log/utility/setup/console.hpp>
#include <boost/log/expressions/keyword_fwd.hpp>
#include <boost/log/expressions/keyword.hpp>
#include <boost/log/attributes/clock.hpp>
#include <string>
#include <iostream>

namespace logging = boost::log;
namespace sinks = boost::log::sinks;
namespace src = boost::log::sources;
namespace expr = boost::log::expressions;
namespace attrs = boost::log::attributes;
namespace keywords = boost::log::keywords;

#define tlog BOOST_LOG_TRIVIAL(trace)
#define dlog BOOST_LOG_TRIVIAL(debug)
#define ilog BOOST_LOG_TRIVIAL(info)
#define wlog BOOST_LOG_TRIVIAL(warning)
#define elog BOOST_LOG_TRIVIAL(error)
#define flog BOOST_LOG_TRIVIAL(fatal)

#define tout tlog
#define dout dlog
#define iout ilog
#define wout wlog
#define eout elog
#define fout flog

BOOST_LOG_ATTRIBUTE_KEYWORD(a_timestamp, "TimeStamp", attrs::local_clock::value_type)
BOOST_LOG_ATTRIBUTE_KEYWORD(a_thread_id, "ThreadID", attrs::current_thread_id::value_type)

static void init_logging(std::string file){
#ifndef ENGINE_LOGGING_INIT_COMPLETE
    logging::register_simple_formatter_factory<logging::trivial::severity_level, char>("Severity");

    std::string _format = "[%TimeStamp%] [%ThreadID%] [%Severity%]: %Message%";

    logging::add_file_log(
            keywords::file_name = "logs/%Y-%m-%d_%H:%M:%S_%N-" + file + ".log",
            keywords::rotation_size = 10 * 1024 * 1024, // 10mb
            keywords::time_based_rotation = sinks::file::rotation_at_time_point(0, 0, 0), // or at midnight
            keywords::format = _format
    );

    logging::add_console_log(std::cout, boost::log::keywords::format = _format);

    logging::add_common_attributes();

#define ENGINE_LOGGING_INIT_COMPLETE
#endif
}

#endif //ENGINE_LOGGING_H
