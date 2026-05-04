'use client'
import React from 'react'

interface HamburgerButtonProps {
    onOpen: () => void;
    isOpen: boolean;
}

const HamburgerButton = ({ onOpen, isOpen }: HamburgerButtonProps) => {
    return (
        <div className="md:hidden"> {/* Wrapper for the burger button */}
            <button 
                onClick={onOpen}
                className="h-full aspect-square flex justify-center items-center p-1 rounded-full border border-transparent transition cursor-pointer hover:bg-theme2 hover:text-text hover:border-highlight"
                aria-label="Open menu"
                aria-expanded={isOpen}
            >
                <svg width="1.33em" height="1.33em" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="3" y1="12" x2="21" y2="12"></line><line x1="3" y1="6" x2="21" y2="6"></line><line x1="3" y1="18" x2="21" y2="18"></line></svg>
            </button>
        </div>
    )
}

export default HamburgerButton
