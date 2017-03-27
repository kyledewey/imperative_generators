#!/usr/bin/perl -w

package ListIterator;

use Iterator;
use warnings;
use strict;

our @ISA = qw(Iterator);

# -The minimum list length
# -The maximum list length
# -Constructor for an Iterator with which to fill each position
sub new {
    my ($class, $min, $max, $itCons) = @_;
    if ($min > $max) {
        die "Minimum list length must be <= maximum list length";
    }

    my $self = $class->SUPER::new();
    $self->{_min} = $min;
    $self->{_max} = $max;
    $self->{_itCons} = $itCons;

    $self->initializeWithMinimumSize();

    bless($self, $class);

    return $self;
}

sub initializeWithMinimumSize {
    my $self = shift();
    $self->initializeWithSize($self->{_min});
}

sub initializeWithSize {
    my ($self, $size) = @_;
    
    my @list;
    my @iterators;
    for(my $x = 0; $x < $size; $x++) {
        my $iterator = $self->{_itCons}->();
        if (!$iterator->hasNext()) {
            $self->{_hasNext} = undef;
            return;
        }
        push(@list, $iterator->getNext());
        push(@iterators, $iterator);
    }

    $self->{_iterators} = \@iterators;
    $self->{_list} = \@list;
    $self->{_hasNext} = 1;
}

sub hasNext {
    my $self = shift();
    return $self->{_hasNext};
}

sub getNext {
    my $self = shift();
    if (!$self->hasNext()) {
        die "getNext called on empty iterator";
    }

    my @retval = @{$self->{_list}};
    my $size = scalar(@retval);

    # Increment the rightmost one.  If that cannot be incremented,
    # go one position to the left
    my $hadIncrement = undef;
    for(my $x = $size - 1; $x >= 0; $x--) {
        if ($self->{_iterators}->[$x]->hasNext()) {
            # increment it
            my $n = $self->{_iterators}->[$x]->getNext();
            $self->{_list}->[$x] = $n;
            $hadIncrement = 1;
            last;
        } else {
            # reset this and hope that we can increment somewhere
            # on the left
            $self->{_iterators}->[$x]->reset();
        }
    }

    if (!$hadIncrement) {
        # try to increment the size of the list
        if ($size < $self->{_max}) {
            $self->initializeWithSize($size + 1);
        } else {
            # cannot expand the size of the list, and we incremented
            # everything
            $self->{_hasNext} = undef;
        }
    }

    return \@retval;
}

sub reset {
    my $self = shift();
    $self->initializeWithMinimumSize();
}

1;
