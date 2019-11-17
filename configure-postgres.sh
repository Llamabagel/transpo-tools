#!/bin/bash
psql -c "CREATE USER transpo WITH PASSWORD 'packaging';"
psql -c "CREATE DATABASE transit OWNER transpo;"