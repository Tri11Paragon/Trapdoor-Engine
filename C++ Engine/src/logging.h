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
#include <config.h>

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

#ifdef DEBUG_ENABLED
    extern std::stringstream td_logStream;
#endif

static void td_coloring_formatter(logging::record_view const& rec, logging::formatting_ostream& strm){
    auto severity = rec[logging::trivial::severity];
    if (severity) {
        // Set the color
        switch (severity.get()) {
            case logging::trivial::severity_level::trace:
                strm << "\033[97m"; // 37
#ifdef DEBUG_ENABLED
                td_logStream << "\033[97m"; // 37
#endif
                break;
            case logging::trivial::severity_level::debug:
                strm << "\033[36m";
#ifdef DEBUG_ENABLED
                td_logStream << "\033[36m";
#endif
                break;
            case logging::trivial::severity_level::info:
                strm << "\033[92m";
#ifdef DEBUG_ENABLED
                td_logStream << "\033[92m";
#endif
                break;
            case logging::trivial::severity_level::warning:
                strm << "\033[93m";
#ifdef DEBUG_ENABLED
                td_logStream << "\033[93m";
#endif
                break;
            case logging::trivial::severity_level::error:
                strm << "\033[91m";
#ifdef DEBUG_ENABLED
                td_logStream << "\033[91m";
#endif
                break;
            case logging::trivial::severity_level::fatal:
                strm << "\033[97;41m";
#ifdef DEBUG_ENABLED
                td_logStream << "\033[97;41m";
#endif
                break;
            default:
                break;
        }
    }

    // evil hacks
    auto timestamp = rec[a_timestamp];
    auto date = timestamp->date();
    auto time = timestamp->time_of_day();

    long month = date.month().as_number();
    long day = date.day().as_number();
    long hour = time.hours();
    long minute = time.minutes();
    long seconds = time.seconds();

    std::stringstream strBuild;

    strBuild << "[";
    strBuild << date.year();
    strBuild << "-";
    if (month < 10)
        strBuild << "0";
    strBuild << month;
    strBuild << "-";
    if (day < 10)
        strBuild << "0";
    strBuild << day;
    strBuild << " ";
    if (hour < 10)
        strBuild << "0";
    strBuild << hour;
    strBuild << ":";
    if (minute < 10)
        strBuild << "0";
    strBuild << minute;
    strBuild << ":";
    if (seconds < 10)
        strBuild << "0";
    strBuild << seconds;
    strBuild << ".";
    strBuild << time.ticks();
    strBuild << "] ";

    strm << strBuild.str();
    strm << "[" << rec[a_thread_id] << "] ";
    strm << "[" << severity << "]: ";
#ifdef DEBUG_ENABLED
    td_logStream << strBuild.str();
    td_logStream << "[" << rec[a_thread_id] << "] ";
    td_logStream << "[" << severity << "]: ";
#endif

    auto theMessage = rec[logging::expressions::smessage];
    if (theMessage->ends_with('\n')) {
        strm << theMessage->substr(0, theMessage->size() - 1);
#ifdef DEBUG_ENABLED
        td_logStream << theMessage;
#endif
    } else {
        strm << theMessage;
#ifdef DEBUG_ENABLED
        td_logStream << theMessage << '\n';
#endif
    }

    if (severity) {
        // Restore the default color
        strm << "\033[0m";
#ifdef DEBUG_ENABLED
        td_logStream << "\033[0m";
#endif
    }

}


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

    auto log = logging::add_console_log(std::cout, boost::log::keywords::format = _format);
    log->set_formatter(&td_coloring_formatter);

    logging::add_common_attributes();

#define ENGINE_LOGGING_INIT_COMPLETE
#endif
}

#endif //ENGINE_LOGGING_H
