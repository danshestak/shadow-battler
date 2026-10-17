import { STATUS_CODES } from 'http'
import { NextResponse } from 'next/server'
import 'server-only'

export const errorResponse = (status: number, message?: string) => {
    return NextResponse.json(
        {
            error: STATUS_CODES[status],
            status: status,
            message: message
        },
        {
            status: status
        }
    )
}